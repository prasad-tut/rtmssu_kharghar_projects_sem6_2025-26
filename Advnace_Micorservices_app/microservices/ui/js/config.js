// API Configuration
const API_BASE = '/api';

// Session Management
const Session = {
    set: (key, value) => {
        sessionStorage.setItem(key, JSON.stringify(value));
    },
    get: (key) => {
        const item = sessionStorage.getItem(key);
        return item ? JSON.parse(item) : null;
    },
    remove: (key) => {
        sessionStorage.removeItem(key);
    },
    clear: () => {
        sessionStorage.clear();
    },
    getUser: () => {
        return Session.get('currentUser');
    },
    setUser: (user) => {
        if (user && user.userId && !user.id) {
            user.id = user.userId;
        }
        Session.set('currentUser', user);
    },
    isLoggedIn: () => {
        return Session.getUser() !== null;
    }
};

// Toast Notification
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) return;

    // Use innerHTML to support <br> if we use it, or just span for wrapping
    toast.innerHTML = `<span>${message.replace(/\n/g, '<br>')}</span>`;

    // Add icon based on type
    const icon = document.createElement('i');
    if (type === 'success') icon.className = 'fas fa-check-circle';
    else if (type === 'error') icon.className = 'fas fa-exclamation-circle';
    else if (type === 'warning') icon.className = 'fas fa-exclamation-triangle';
    else icon.className = 'fas fa-info-circle';

    toast.prepend(icon);

    toast.className = `toast ${type} show`;

    // Auto hide after some time
    setTimeout(() => {
        toast.classList.remove('show');
    }, 5000); // Increased to 5s to allow reading multi-line errors
}

// API Helper
async function apiCall(endpoint, options = {}) {
    try {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({ error: 'Request failed' }));

            let message = error.message || error.error || 'Request failed';

            // Handle validation details if present
            if (error.details && Array.isArray(error.details) && error.details.length > 0) {
                message = error.details.join('\n');
            }

            throw new Error(message);
        }

        const text = await response.text();
        return text ? JSON.parse(text) : {};
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Format date
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Get initials from name
function getInitials(name) {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
}

// Check auth and redirect
function checkAuth(requiredRole) {
    const user = Session.getUser();
    if (!user) {
        window.location.href = 'index.html';
        return null;
    }
    if (requiredRole && user.role !== requiredRole) {
        window.location.href = 'index.html';
        return null;
    }
    return user;
}

// Logout
async function logout() {
    Loading.show();
    const user = Session.getUser();

    // Force redirect backup - if API takes too long, just go
    const safetyRedirect = setTimeout(() => {
        Session.clear();
        window.location.href = 'index.html';
    }, 2000);

    if (user && user.email) {
        try {
            await apiCall(`/auth/logout?email=${encodeURIComponent(user.email)}`, { method: 'POST' });
        } catch (e) {
            console.error('Logout error:', e);
        }
    }

    // If API finishes fast, clear timeout and redirect normally
    clearTimeout(safetyRedirect);
    Session.clear();
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 600);
}

// Theme Management
const Theme = {
    init: () => {
        // Check local storage or system preference
        const savedTheme = localStorage.getItem('theme') || 'dark';
        document.documentElement.setAttribute('data-theme', savedTheme);
        Theme.updateIcon(savedTheme);
    },
    toggle: () => {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        Theme.updateIcon(newTheme);
    },
    updateIcon: (theme) => {
        const icons = document.querySelectorAll('.theme-icon-display');
        icons.forEach(icon => {
            icon.className = theme === 'light' ? 'fas fa-moon theme-icon-display' : 'fas fa-sun theme-icon-display';
        });

        // Backwards compatibility if using ID
        const oldIcon = document.getElementById('theme-icon');
        if (oldIcon) oldIcon.className = theme === 'light' ? 'fas fa-moon' : 'fas fa-sun';
    }
};

// Custom Loader HTML Generator
function getLoaderHtml() {
    return `
        <div class="loader-wrapper">
            <span class="loader-letter">H</span>
            <span class="loader-letter">a</span>
            <span class="loader-letter">n</span>
            <span class="loader-letter">g</span>
            <span class="loader-letter"> </span>
            <span class="loader-letter">o</span>
            <span class="loader-letter">n</span>
            <span class="loader-letter"> </span>
            <span class="loader-letter">t</span>
            <span class="loader-letter">o</span>
            <span class="loader-letter"> </span>
            <span class="loader-letter">y</span>
            <span class="loader-letter">o</span>
            <span class="loader-letter">u</span>
            <span class="loader-letter">r</span>
            <span class="loader-letter"> </span>
            <span class="loader-letter">t</span>
            <span class="loader-letter">i</span>
            <span class="loader-letter">c</span>
            <span class="loader-letter">k</span>
            <span class="loader-letter">e</span>
            <span class="loader-letter">t</span>
            <div class="loader"></div>
        </div>
    `;
}

// Global Loading Management
const Loading = {
    show: () => {
        let overlay = document.getElementById('global-loader');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.id = 'global-loader';
            overlay.className = 'loading-overlay';
            overlay.innerHTML = `
                <div class="loader-container">
                    ${getLoaderHtml()}
                </div>
            `;
            document.body.appendChild(overlay);
        }
        // Force reflow
        overlay.offsetHeight;
        overlay.classList.add('active');
    },
    hide: () => {
        const overlay = document.getElementById('global-loader');
        if (overlay) {
            overlay.classList.remove('active');
        }
    }
};

// Skeleton Loader HTML Generator
function getSkeletonRowsHtml(columnCount, rowCount = 5) {
    let rows = '';
    for (let i = 0; i < rowCount; i++) {
        rows += `<tr class="skeleton-row">`;
        for (let j = 0; j < columnCount; j++) {
            rows += `<td><div class="skeleton"></div></td>`;
        }
        rows += `</tr>`;
    }
    return rows;
}

// Initialize theme on load
document.addEventListener('DOMContentLoaded', Theme.init);
