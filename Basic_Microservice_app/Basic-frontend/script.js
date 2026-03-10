// API Configuration
const API_BASE_URL = 'http://localhost:9090'; // API Gateway URL
const API_ENDPOINTS = {
    users: `${API_BASE_URL}/user-micro-service/users`,
    tickets: `${API_BASE_URL}/ticket-service/tickets`
};

// Global state
let allUsers = [];
let allTickets = [];
let isLoadingUsers = false;
let isLoadingTickets = false;

// ===========================
// INITIALIZATION
// ===========================
document.addEventListener('DOMContentLoaded', () => {
    console.log('Page loaded, fetching data...');
    loadUsers();
    loadTickets();
});

// ===========================
// DATA LOADING
// ===========================
async function loadUsers() {
    if (isLoadingUsers) {
        console.log('Already loading users, skipping...');
        return;
    }

    isLoadingUsers = true;
    console.log('Loading users from:', API_ENDPOINTS.users);

    try {
        const response = await fetch(API_ENDPOINTS.users, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        console.log('Users response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        allUsers = await response.json();
        console.log('Loaded users:', allUsers.length);
        displayUsers(allUsers);
        updateStats();
        populateUserDropdowns();
    } catch (error) {
        console.error('Error loading users:', error);
        showEmptyState('usersTableBody', 'No users found', 'users');
        // Don't show error toast on initial load to avoid spam
    } finally {
        isLoadingUsers = false;
    }
}

async function loadTickets() {
    if (isLoadingTickets) {
        console.log('Already loading tickets, skipping...');
        return;
    }

    isLoadingTickets = true;
    console.log('Loading tickets from:', API_ENDPOINTS.tickets);

    try {
        const response = await fetch(API_ENDPOINTS.tickets, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        console.log('Tickets response status:', response.status);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        allTickets = await response.json();
        console.log('Loaded tickets:', allTickets.length);
        displayTickets(allTickets);
        updateStats();
    } catch (error) {
        console.error('Error loading tickets:', error);
        showEmptyState('ticketsTableBody', 'No tickets found', 'tickets');
        // Don't show error toast on initial load to avoid spam
    } finally {
        isLoadingTickets = false;
    }
}

function refreshData() {
    loadUsers();
    loadTickets();
    showToast('Data refreshed successfully', 'success');
}

// ===========================
// DISPLAY FUNCTIONS
// ===========================
function displayUsers(users) {
    const tbody = document.getElementById('usersTableBody');

    if (!users || users.length === 0) {
        showEmptyState('usersTableBody', 'No users found', 'users');
        return;
    }

    tbody.innerHTML = users.map(user => `
        <tr>
            <td><strong>#${user.id}</strong></td>
            <td>${escapeHtml(user.name)}</td>
            <td>${escapeHtml(user.email)}</td>
            <td>${escapeHtml(user.phone)}</td>
            <td><span class="badge badge-${user.role.toLowerCase()}">${user.role}</span></td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-sm btn-secondary" onclick="editUser(${user.id})">
                        <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                        Edit
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deleteUser(${user.id})">
                        <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                        Delete
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function displayTickets(tickets) {
    const tbody = document.getElementById('ticketsTableBody');

    if (!tickets || tickets.length === 0) {
        showEmptyState('ticketsTableBody', 'No tickets found', 'tickets');
        return;
    }

    tbody.innerHTML = tickets.map(ticket => {
        const raisedByUser = allUsers.find(u => u.id === ticket.raisedBy);
        const assignedToUser = ticket.assignedTo ? allUsers.find(u => u.id === ticket.assignedTo) : null;

        return `
            <tr>
                <td><strong>#${ticket.id}</strong></td>
                <td>${escapeHtml(ticket.issue)}</td>
                <td>${raisedByUser ? escapeHtml(raisedByUser.name) : `User #${ticket.raisedBy}`}</td>
                <td>${assignedToUser ? escapeHtml(assignedToUser.name) : '<span style="color: var(--gray-400);">Unassigned</span>'}</td>
                <td><span class="badge badge-${ticket.status.toLowerCase()}">${ticket.status}</span></td>
                <td>${formatDate(ticket.raisedOn)}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn btn-sm btn-secondary" onclick="editTicket(${ticket.id})">
                            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                            Edit
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteTicket(${ticket.id})">
                            <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                            Delete
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function showEmptyState(tbodyId, message, type) {
    const tbody = document.getElementById(tbodyId);
    const icon = type === 'users'
        ? '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />'
        : '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" />';

    tbody.innerHTML = `
        <tr>
            <td colspan="${type === 'users' ? 6 : 7}">
                <div class="empty-state">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        ${icon}
                    </svg>
                    <p>${message}</p>
                </div>
            </td>
        </tr>
    `;
}

// ===========================
// STATS UPDATE
// ===========================
function updateStats() {
    document.getElementById('totalUsers').textContent = allUsers.length;
    document.getElementById('openTickets').textContent = allTickets.filter(t => t.status === 'OPEN').length;
    document.getElementById('assignedTickets').textContent = allTickets.filter(t => t.status === 'ASSIGNED').length;
    document.getElementById('closedTickets').textContent = allTickets.filter(t => t.status === 'CLOSED').length;
}

// ===========================
// TAB SWITCHING
// ===========================
function switchTab(tabName) {
    // Update tab buttons
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.closest('.tab-btn').classList.add('active');

    // Update tab content
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    document.getElementById(`${tabName}Tab`).classList.add('active');
}

// ===========================
// SEARCH/FILTER
// ===========================
function filterUsers() {
    const searchTerm = document.getElementById('userSearch').value.toLowerCase();
    const filteredUsers = allUsers.filter(user =>
        user.name.toLowerCase().includes(searchTerm) ||
        user.email.toLowerCase().includes(searchTerm) ||
        user.phone.includes(searchTerm) ||
        user.role.toLowerCase().includes(searchTerm)
    );
    displayUsers(filteredUsers);
}

function filterTickets() {
    const searchTerm = document.getElementById('ticketSearch').value.toLowerCase();
    const filteredTickets = allTickets.filter(ticket => {
        const raisedByUser = allUsers.find(u => u.id === ticket.raisedBy);
        const assignedToUser = ticket.assignedTo ? allUsers.find(u => u.id === ticket.assignedTo) : null;

        return ticket.issue.toLowerCase().includes(searchTerm) ||
            ticket.status.toLowerCase().includes(searchTerm) ||
            (raisedByUser && raisedByUser.name.toLowerCase().includes(searchTerm)) ||
            (assignedToUser && assignedToUser.name.toLowerCase().includes(searchTerm));
    });
    displayTickets(filteredTickets);
}

// ===========================
// MODAL FUNCTIONS
// ===========================
function showCreateUserModal() {
    document.getElementById('createUserModal').classList.add('active');
    document.getElementById('createUserForm').reset();
}

function showCreateTicketModal() {
    document.getElementById('createTicketModal').classList.add('active');
    document.getElementById('createTicketForm').reset();
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

// Close modal on escape key
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal').forEach(modal => modal.classList.remove('active'));
    }
});

// ===========================
// CREATE FUNCTIONS
// ===========================
async function createUser(event) {
    event.preventDefault();

    const userData = {
        name: document.getElementById('userName').value,
        email: document.getElementById('userEmail').value,
        phone: document.getElementById('userPhone').value,
        role: document.getElementById('userRole').value
    };

    try {
        const response = await fetch(API_ENDPOINTS.users, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        if (!response.ok) throw new Error('Failed to create user');

        closeModal('createUserModal');
        showToast('User created successfully', 'success');
        loadUsers();
    } catch (error) {
        console.error('Error creating user:', error);
        showToast('Failed to create user', 'error');
    }
}

async function createTicket(event) {
    event.preventDefault();

    const ticketData = {
        issue: document.getElementById('ticketIssue').value,
        raisedBy: parseInt(document.getElementById('ticketRaisedBy').value),
        assignedTo: document.getElementById('ticketAssignedTo').value ? parseInt(document.getElementById('ticketAssignedTo').value) : null,
        status: document.getElementById('ticketStatus').value
    };

    try {
        const response = await fetch(API_ENDPOINTS.tickets, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ticketData)
        });

        if (!response.ok) throw new Error('Failed to create ticket');

        closeModal('createTicketModal');
        showToast('Ticket created successfully', 'success');
        loadTickets();
    } catch (error) {
        console.error('Error creating ticket:', error);
        showToast('Failed to create ticket', 'error');
    }
}

// ===========================
// EDIT FUNCTIONS
// ===========================
function editUser(userId) {
    const user = allUsers.find(u => u.id === userId);
    if (!user) return;

    // Populate form with user data
    document.getElementById('userName').value = user.name;
    document.getElementById('userEmail').value = user.email;
    document.getElementById('userPhone').value = user.phone;
    document.getElementById('userRole').value = user.role;

    // Change form submit to update
    const form = document.getElementById('createUserForm');
    form.onsubmit = async (e) => {
        e.preventDefault();
        await updateUser(userId);
    };

    showCreateUserModal();
}

async function updateUser(userId) {
    const userData = {
        name: document.getElementById('userName').value,
        email: document.getElementById('userEmail').value,
        phone: document.getElementById('userPhone').value,
        role: document.getElementById('userRole').value
    };

    try {
        const response = await fetch(`${API_ENDPOINTS.users}/${userId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        if (!response.ok) throw new Error('Failed to update user');

        closeModal('createUserModal');
        showToast('User updated successfully', 'success');
        loadUsers();

        // Reset form submit
        document.getElementById('createUserForm').onsubmit = createUser;
    } catch (error) {
        console.error('Error updating user:', error);
        showToast('Failed to update user', 'error');
    }
}

function editTicket(ticketId) {
    const ticket = allTickets.find(t => t.id === ticketId);
    if (!ticket) return;

    // Populate form with ticket data
    document.getElementById('ticketIssue').value = ticket.issue;
    document.getElementById('ticketRaisedBy').value = ticket.raisedBy;
    document.getElementById('ticketAssignedTo').value = ticket.assignedTo || '';
    document.getElementById('ticketStatus').value = ticket.status;

    // Change form submit to update
    const form = document.getElementById('createTicketForm');
    form.onsubmit = async (e) => {
        e.preventDefault();
        await updateTicket(ticketId);
    };

    showCreateTicketModal();
}

async function updateTicket(ticketId) {
    const ticketData = {
        issue: document.getElementById('ticketIssue').value,
        raisedBy: parseInt(document.getElementById('ticketRaisedBy').value),
        assignedTo: document.getElementById('ticketAssignedTo').value ? parseInt(document.getElementById('ticketAssignedTo').value) : null,
        status: document.getElementById('ticketStatus').value
    };

    try {
        const response = await fetch(`${API_ENDPOINTS.tickets}/${ticketId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ticketData)
        });

        if (!response.ok) throw new Error('Failed to update ticket');

        closeModal('createTicketModal');
        showToast('Ticket updated successfully', 'success');
        loadTickets();

        // Reset form submit
        document.getElementById('createTicketForm').onsubmit = createTicket;
    } catch (error) {
        console.error('Error updating ticket:', error);
        showToast('Failed to update ticket', 'error');
    }
}

// ===========================
// DELETE FUNCTIONS
// ===========================
async function deleteUser(userId) {
    if (!confirm('Are you sure you want to delete this user?')) return;

    try {
        const response = await fetch(`${API_ENDPOINTS.users}/${userId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to delete user');

        showToast('User deleted successfully', 'success');
        loadUsers();
    } catch (error) {
        console.error('Error deleting user:', error);
        showToast('Failed to delete user', 'error');
    }
}

async function deleteTicket(ticketId) {
    if (!confirm('Are you sure you want to delete this ticket?')) return;

    try {
        const response = await fetch(`${API_ENDPOINTS.tickets}/${ticketId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to delete ticket');

        showToast('Ticket deleted successfully', 'success');
        loadTickets();
    } catch (error) {
        console.error('Error deleting ticket:', error);
        showToast('Failed to delete ticket', 'error');
    }
}

// ===========================
// UTILITY FUNCTIONS
// ===========================
function populateUserDropdowns() {
    const raisedBySelect = document.getElementById('ticketRaisedBy');
    const assignedToSelect = document.getElementById('ticketAssignedTo');

    const userOptions = allUsers.map(user =>
        `<option value="${user.id}">${user.name} (${user.role})</option>`
    ).join('');

    raisedBySelect.innerHTML = '<option value="">Select user</option>' + userOptions;
    assignedToSelect.innerHTML = '<option value="">Not assigned</option>' + userOptions;
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;

    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}
