// Auth page functions

// Show signup page
function showSignup() {
    // Clear any existing session
    Session.clear();
    // Clear form fields
    document.getElementById('signupForm').reset();
    document.getElementById('loginPage').classList.remove('active');
    document.getElementById('signupPage').classList.add('active');
}

// Show login page
function showLogin() {
    // Clear any existing session
    Session.clear();
    // Clear form fields
    document.getElementById('loginForm').reset();
    document.getElementById('signupForm').reset();
    document.getElementById('signupPage').classList.remove('active');
    document.getElementById('loginPage').classList.add('active');
}

// Login
async function login(event) {
    event.preventDefault();

    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    const btn = event.target.querySelector('button[type="submit"]');

    console.log('Login attempt for:', email);

    Loading.show();
    btn.disabled = true;

    // Timeout protection - reset button after 10 seconds if no response
    // Timeout protection - reset button after 70 seconds if no response
    const timeoutId = setTimeout(() => {
        console.error('Login timeout - no response after 70 seconds');
        Loading.hide();
        btn.disabled = false;
        btn.innerHTML = '<span>Sign In</span><i class="fas fa-arrow-right"></i>';
        showToast('Connection timeout. The server is taking longer than expected. Please try again.', 'error');
    }, 70000);

    try {
        console.log('Sending login request to:', '/auth/login');
        const user = await apiCall('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });

        clearTimeout(timeoutId);
        console.log('Login successful:', user);

        Session.setUser(user);
        showToast('Login successful!', 'success');

        // Redirect based on role
        setTimeout(() => {
            Loading.hide();
            if (user.role === 'CUSTOMER') {
                window.location.href = 'customer.html';
            } else if (user.role === 'EXECUTIVE') {
                window.location.href = 'executive.html';
            } else if (user.role === 'ADMIN') {
                window.location.href = 'admin.html';
            }
        }, 500);

    } catch (error) {
        clearTimeout(timeoutId);
        Loading.hide();
        console.error('Login error:', error);
        showToast(error.message || 'Login failed. Please check your credentials.', 'error');
        btn.disabled = false;
        btn.innerHTML = '<span>Sign In</span><i class="fas fa-arrow-right"></i>';
    }
}

// Signup
async function signup(event) {
    event.preventDefault();

    const userData = {
        name: document.getElementById('signupName').value,
        email: document.getElementById('signupEmail').value,
        phone: document.getElementById('signupPhone').value,
        address: document.getElementById('signupAddress').value,
        password: document.getElementById('signupPassword').value,
        role: 'CUSTOMER'
    };

    const btn = event.target.querySelector('button[type="submit"]');
    Loading.show();
    btn.disabled = true;

    try {
        await apiCall('/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData)
        });

        showToast('Account created successfully! Please login.', 'success');
        // Clear the signup form
        document.getElementById('signupForm').reset();
        setTimeout(() => showLogin(), 1000);

    } catch (error) {
        showToast(error.message || 'Signup failed', 'error');
    } finally {
        Loading.hide();
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-user-plus"></i> Create Account';
    }
}

// Check if already logged in
document.addEventListener('DOMContentLoaded', () => {
    // Clear forms on page load
    const loginForm = document.getElementById('loginForm');
    const signupForm = document.getElementById('signupForm');
    if (loginForm) loginForm.reset();
    if (signupForm) signupForm.reset();

    const user = Session.getUser();
    if (user) {
        if (user.role === 'CUSTOMER') {
            window.location.href = 'customer.html';
        } else if (user.role === 'EXECUTIVE') {
            window.location.href = 'executive.html';
        } else if (user.role === 'ADMIN') {
            window.location.href = 'admin.html';
        }
    }
});
