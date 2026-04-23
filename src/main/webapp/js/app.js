/**
 * AabhushanAI — Core API & UI Layer
 * Production-ready, guest-safe, full error handling
 */

// Detect context path dynamically (works at /AabhushanAI/pages/...)
const _path = window.location.pathname;
const _ctxEnd = _path.indexOf('/pages');
const contextPath = _ctxEnd !== -1 ? _path.substring(0, _ctxEnd) : '';
const API_BASE = `${contextPath}/api`;

const API = {
    async call(endpoint, method = 'GET', data = null) {
        const options = {
            method,
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin'   // <-- CRITICAL: send session cookie
        };
        if (data !== null) options.body = JSON.stringify(data);

        try {
            const response = await fetch(`${API_BASE}${endpoint}`, options);
            const text = await response.text();
            if (!text) return { success: false, message: 'Empty response from server' };

            let result;
            try { result = JSON.parse(text); }
            catch (e) { throw new Error('Server returned non-JSON: ' + text.substring(0, 100)); }

            if (!response.ok) {
                const msg = result.message || `HTTP ${response.status}`;
                throw new Error(msg);
            }
            return result;
        } catch (error) {
            console.error(`[API] ${method} ${API_BASE}${endpoint} →`, error.message);
            throw error;
        }
    },

    // ── Auth ─────────────────────────────────────────────────────────────
    login:        (email, password) => API.call('/auth/login',  'POST', { email, password }),
    signup:       (user)            => API.call('/auth/signup', 'POST', user),
    logout:       ()                => API.call('/auth/logout', 'POST'),
    checkSession: ()                => API.call('/auth/session'),

    // ── Products ─────────────────────────────────────────────────────────
    getProducts: (filters = {}) => {
        const params = new URLSearchParams(
            Object.fromEntries(Object.entries(filters).filter(([,v]) => v !== null && v !== undefined && v !== ''))
        ).toString();
        return API.call(`/products${params ? '?' + params : ''}`);
    },
    searchProducts: (q)  => API.call(`/products?q=${encodeURIComponent(q)}`),
    getProduct:    (id)  => API.call(`/products/item/${id}`),

    // ── Cart ─────────────────────────────────────────────────────────────
    getCart:         ()               => API.call('/cart'),
    addToCart:       (productId, qty) => API.call('/cart/add',          'POST', { productId, quantity: qty || 1 }),
    updateCart:      (cartId, qty)    => API.call('/cart/update',       'POST', { cartId,    quantity: qty }),
    removeFromCart:  (cartId)         => API.call(`/cart/remove/${cartId}`, 'POST'),

    // ── Orders ───────────────────────────────────────────────────────────
    placeOrder:      (orderData) => API.call('/orders', 'POST', orderData || {}),
    getOrderHistory: ()          => API.call('/orders'),

    // ── Locker ───────────────────────────────────────────────────────────
    getLocker:        ()      => API.call('/locker'),
    saveToLocker:     (item)  => API.call('/locker/add',        'POST', item),
    removeFromLocker: (id)    => API.call(`/locker/remove/${id}`, 'POST'),

    // ── Bargain ──────────────────────────────────────────────────────────
    getBargains:  ()         => API.call('/bargains'),
    negotiate:    (bargain)  => API.call('/bargains/negotiate', 'POST', bargain),

    // ── Admin ────────────────────────────────────────────────────────────
    adminGetProducts:    ()        => API.call('/admin/products'),
    adminAddProduct:     (product) => API.call('/admin/products/add',         'POST',   product),
    adminUpdateProduct:  (product) => API.call('/admin/products/update',      'PUT',    product),
    adminDeleteProduct:  (id)      => API.call(`/admin/products/delete/${id}`, 'DELETE')
};

const UI = {
    showToast(message, type = 'success') {
        // Remove existing toasts
        document.querySelectorAll('.aai-toast').forEach(t => t.remove());

        const toast = document.createElement('div');
        toast.className = `aai-toast fixed bottom-8 right-8 px-6 py-4 shadow-2xl z-[9999]
            font-bold tracking-widest uppercase text-xs
            transition-all duration-500 transform translate-y-20 opacity-0
            ${type === 'success'
                ? 'bg-primary text-on-primary'
                : 'bg-red-800 text-white border border-red-500'}`;
        toast.innerText = message;
        document.body.appendChild(toast);

        requestAnimationFrame(() => {
            toast.classList.remove('translate-y-20', 'opacity-0');
        });
        setTimeout(() => {
            toast.classList.add('translate-y-20', 'opacity-0');
            setTimeout(() => toast.remove(), 500);
        }, 3500);
    },

    showLoading() {
        if (document.getElementById('page-loader')) return;
        const loader = document.createElement('div');
        loader.id = 'page-loader';
        loader.className = 'fixed inset-0 bg-black/60 backdrop-blur-sm z-[9998] flex items-center justify-center';
        loader.innerHTML = '<div class="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>';
        document.body.appendChild(loader);
    },

    hideLoading() {
        const loader = document.getElementById('page-loader');
        if (loader) loader.remove();
    },

    updateNavForUser(user) {
        const profileLink = document.getElementById('nav-profile-link');
        const cartLink    = document.getElementById('nav-cart-link');
        const logoutBtn   = document.getElementById('nav-logout-btn');
        const loginLink   = document.getElementById('nav-login-link');

        if (user) {
            if (profileLink) profileLink.style.display = '';
            if (cartLink)    cartLink.style.display    = '';
            if (logoutBtn)   logoutBtn.style.display   = '';
            if (loginLink)   loginLink.style.display   = 'none';
        } else {
            if (profileLink) profileLink.style.display = 'none';
            if (logoutBtn)   logoutBtn.style.display   = 'none';
            if (loginLink)   loginLink.style.display   = '';
        }
    },

    toggleLanguage() {
        const btn = document.getElementById('lang-btn-text');
        const isEn = btn.innerText === 'EN';
        const newLang = isEn ? 'HI' : 'EN';
        btn.innerText = newLang;
        
        const translations = {
            'EN': { 'shop': 'Collections', 'bespoke': 'Bespoke', 'atelier': 'Atelier' },
            'HI': { 'shop': 'संग्रह', 'bespoke': 'अनुकूलित', 'atelier': 'कार्यशाला' }
        };
        
        document.querySelectorAll('[data-i18n]').forEach(el => {
            const key = el.dataset.i18n;
            if (translations[newLang][key]) el.innerText = translations[newLang][key];
        });
        
        this.showToast(`Language switched to ${newLang === 'EN' ? 'English' : 'Hindi'}`);
    }
};

// ── Auto session check on page load ──────────────────────────────────────────
window.currentUser = null;

document.addEventListener('DOMContentLoaded', async () => {
    const page = window.location.pathname;

    // Pages that don't need session check at all
    if (page.includes('login.html')) return;

    // Protected pages: redirect to login if not authenticated
    const protectedPages = ['cart.html', 'checkout.html', 'orders.html', 'admin.html'];
    const isProtected = protectedPages.some(p => page.includes(p));

    try {
        const res = await API.checkSession();
        if (res.loggedIn) {
            window.currentUser = res.user;
            document.body.classList.add('logged-in');
            UI.updateNavForUser(res.user);
        } else if (isProtected) {
            window.location.href = 'login.html';
        }
    } catch (err) {
        console.warn('[Session] Check failed:', err.message);
        if (isProtected) window.location.href = 'login.html';
    }
});
