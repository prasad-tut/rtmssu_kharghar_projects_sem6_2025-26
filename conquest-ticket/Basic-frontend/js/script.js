const API_BASE_URL = 'http://localhost:9090';

// Global State
let users = [];
let tickets = [];

// DOM Elements
const menuItems = document.querySelectorAll('.menu-item');
const pages = document.querySelectorAll('.page');
const usersBody = document.getElementById('usersBody');
const ticketsBody = document.getElementById('ticketsBody');
const recentTicketsBody = document.getElementById('recentTicketsBody');
const userForm = document.getElementById('userForm');
const ticketForm = document.getElementById('ticketForm');
const searchInput = document.querySelector('.search-container input');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    loadDashboardData();
    
    // Form Submissions
    userForm.addEventListener('submit', handleUserSubmit);
    ticketForm.addEventListener('submit', handleTicketSubmit);
    searchInput.addEventListener('input', handleSearch);

    // Create New Button
    document.getElementById('btnCreateNew').addEventListener('click', () => {
        openTicketModal();
    });
});

// Navigation
function initNavigation() {
    menuItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const targetPage = item.getAttribute('data-page');
            switchPage(targetPage);
        });
    });

    // Sidebar View All links
    document.querySelectorAll('.view-all').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            switchPage(link.getAttribute('data-page'));
        });
    });
}

function switchPage(pageId) {
    // Update Menu
    menuItems.forEach(item => {
        if (item.getAttribute('data-page') === pageId) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });

    // Update Page
    pages.forEach(page => {
        if (page.id === `${pageId}Page`) {
            page.classList.add('active');
        } else {
            page.classList.remove('active');
        }
    });

    // Load page data
    if (pageId === 'users') loadUsers();
    if (pageId === 'tickets') loadTickets();
}

// API Calls
async function loadDashboardData() {
    await Promise.all([loadUsers(false), loadTickets(false)]);
    updateStats();
    renderRecentTickets();
}

async function loadUsers(render = true) {
    try {
        const response = await fetch(`${API_BASE_URL}/user-micro-service/users`);
        users = await response.json();
        if (render) renderUsers();
        populateUserDropdowns();
    } catch (error) {
        showToast('Error loading users', 'error');
    }
}

async function loadTickets(render = true) {
    try {
        const response = await fetch(`${API_BASE_URL}/ticket-service/tickets`);
        tickets = await response.json();
        if (render) renderTickets();
    } catch (error) {
        showToast('Error loading tickets', 'error');
    }
}

// Rendering
function renderUsers() {
    usersBody.innerHTML = users.map(user => `
        <tr>
            <td>#${user.id}</td>
            <td><strong>${user.name}</strong></td>
            <td>${user.email}</td>
            <td>${user.phone}</td>
            <td><span class="status-pill assigned">${user.role}</span></td>
            <td class="action-btns">
                <button class="btn-icon" onclick="editUser(${user.id})"><i class="fas fa-edit"></i></button>
                <button class="btn-icon delete" onclick="deleteUser(${user.id})"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

function renderTickets() {
    ticketsBody.innerHTML = tickets.map(ticket => `
        <tr>
            <td>#${ticket.id}</td>
            <td>${ticket.issue}</td>
            <td>${getUserName(ticket.raisedBy)}</td>
            <td>${getUserName(ticket.assignedTo) || 'Unassigned'}</td>
            <td><span class="status-pill ${ticket.status.toLowerCase()}">${ticket.status}</span></td>
            <td class="action-btns">
                <button class="btn-icon" onclick="viewTicket(${ticket.id})" title="View Details"><i class="fas fa-eye"></i></button>
                <button class="btn-icon" onclick="editTicket(${ticket.id})" title="Edit"><i class="fas fa-edit"></i></button>
                ${ticket.status !== 'CLOSED' ? `<button class="btn-icon" onclick="closeTicket(${ticket.id})" title="Close Ticket"><i class="fas fa-check"></i></button>` : ''}
                <button class="btn-icon delete" onclick="deleteTicket(${ticket.id})" title="Delete"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

function renderRecentTickets() {
    const recent = tickets.slice(-5).reverse();
    recentTicketsBody.innerHTML = recent.map(ticket => `
        <tr>
            <td>#${ticket.id}</td>
            <td>${ticket.issue}</td>
            <td>${getUserName(ticket.raisedBy)}</td>
            <td><span class="status-pill ${ticket.status.toLowerCase()}">${ticket.status}</span></td>
            <td>${ticket.raisedOn}</td>
        </tr>
    `).join('');
}

function updateStats() {
    document.getElementById('statTotalUsers').textContent = users.length;
    document.getElementById('statOpenTickets').textContent = tickets.filter(t => t.status === 'OPEN').length;
    document.getElementById('statAssignedTickets').textContent = tickets.filter(t => t.status === 'ASSIGNED').length;
    document.getElementById('statClosedTickets').textContent = tickets.filter(t => t.status === 'CLOSED').length;
}

function getUserName(id) {
    if (!id) return '';
    const user = users.find(u => u.id == id);
    return user ? user.name : `User #${id}`;
}

// User Modal
function openUserModal(userId = null) {
    const modal = document.getElementById('userModal');
    const title = document.getElementById('userModalTitle');
    userForm.reset();
    document.getElementById('userId').value = '';

    if (userId) {
        title.textContent = 'Edit User';
        const user = users.find(u => u.id == userId);
        if (user) {
            document.getElementById('userId').value = user.id;
            document.getElementById('userName').value = user.name;
            document.getElementById('userEmail').value = user.email;
            document.getElementById('userPhone').value = user.phone;
            document.getElementById('userRole').value = user.role;
        }
    } else {
        title.textContent = 'Add User';
    }
    modal.style.display = 'block';
}

function closeUserModal() {
    document.getElementById('userModal').style.display = 'none';
}

async function handleUserSubmit(e) {
    e.preventDefault();
    const userId = document.getElementById('userId').value;
    const userData = {
        name: document.getElementById('userName').value,
        email: document.getElementById('userEmail').value,
        phone: document.getElementById('userPhone').value,
        role: document.getElementById('userRole').value
    };

    const method = userId ? 'PUT' : 'POST';
    const url = userId 
        ? `${API_BASE_URL}/user-micro-service/users/${userId}` 
        : `${API_BASE_URL}/user-micro-service/users`;

    try {
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });
        if (response.ok) {
            showToast(`User ${userId ? 'updated' : 'created'} successfully`, 'success');
            closeUserModal();
            loadUsers();
        } else {
            const errData = await response.json().catch(() => ({}));
            console.error('Save user failed:', response.status, errData);
            showToast(`Error: ${response.status} ${errData.message || 'Failed to save user'}`, 'error');
        }
    } catch (error) {
        console.error('Fetch error:', error);
        showToast('Network error or CORS issue. Check console.', 'error');
    }
}

async function deleteUser(id) {
    if (!confirm('Are you sure you want to delete this user?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}/user-micro-service/users/${id}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            showToast('User deleted', 'success');
            loadUsers();
        }
    } catch (error) {
        showToast('Error deleting user', 'error');
    }
}

// Ticket Modal
function openTicketModal(ticketId = null) {
    const modal = document.getElementById('ticketModal');
    const title = document.getElementById('ticketModalTitle');
    ticketForm.reset();
    document.getElementById('ticketId').value = '';
    document.getElementById('assignGroup').style.display = 'block';

    if (ticketId) {
        title.textContent = 'Edit Ticket';
        document.getElementById('assignGroup').style.display = 'block';
        const ticket = tickets.find(t => t.id == ticketId);
        if (ticket) {
            document.getElementById('ticketId').value = ticket.id;
            document.getElementById('ticketIssue').value = ticket.issue;
            document.getElementById('ticketRaisedBy').value = ticket.raisedBy;
            document.getElementById('ticketAssignedTo').value = ticket.assignedTo || '';
            document.getElementById('ticketStatus').value = ticket.status;
        }
    } else {
        title.textContent = 'Raise Ticket';
    }
    modal.style.display = 'block';
}

function closeTicketModal() {
    document.getElementById('ticketModal').style.display = 'none';
}

async function handleTicketSubmit(e) {
    e.preventDefault();
    const ticketId = document.getElementById('ticketId').value;
    const ticketData = {
        issue: document.getElementById('ticketIssue').value,
        raisedBy: parseInt(document.getElementById('ticketRaisedBy').value),
        assignedTo: document.getElementById('ticketAssignedTo').value ? parseInt(document.getElementById('ticketAssignedTo').value) : null,
        status: document.getElementById('ticketStatus').value,
        raisedOn: ticketId ? tickets.find(t => t.id == ticketId).raisedOn : new Date().toISOString().split('T')[0]
    };

    if (ticketData.assignedTo && !ticketId) {
        ticketData.status = 'ASSIGNED';
        ticketData.assignedOn = new Date().toISOString().split('T')[0];
    }

    const method = ticketId ? 'PUT' : 'POST';
    const url = ticketId 
        ? `${API_BASE_URL}/ticket-service/tickets/${ticketId}` 
        : `${API_BASE_URL}/ticket-service/tickets`;

    try {
        const response = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ticketData)
        });
        if (response.ok) {
            showToast(`Ticket ${ticketId ? 'updated' : 'raised'} successfully`, 'success');
            closeTicketModal();
            loadTickets();
            if(!ticketId) updateDashboardStats();
        }
    } catch (error) {
        showToast('Error saving ticket', 'error');
    }
}

async function viewTicket(id) {
    const ticket = tickets.find(t => t.id == id);
    if (!ticket) return;

    const user = users.find(u => u.id == ticket.raisedBy);
    const exec = users.find(u => u.id == ticket.assignedTo);

    const detailHtml = `
        <div class="ticket-detail">
            <p><strong>Status:</strong> <span class="status-pill ${ticket.status.toLowerCase()}">${ticket.status}</span></p>
            <p><strong>Issue:</strong> ${ticket.issue}</p>
            <hr>
            <p><strong>Raised By:</strong> ${user ? `${user.name} (${user.email})` : `User #${ticket.raisedBy}`}</p>
            <p><strong>Raised On:</strong> ${ticket.raisedOn}</p>
            <hr>
            <p><strong>Assigned To:</strong> ${exec ? exec.name : 'Unassigned'}</p>
            <p><strong>Assigned On:</strong> ${ticket.assignedOn || 'N/A'}</p>
        </div>
    `;

    // We can reuse the user modal or create a simple alert for now to show details
    // But better to create a dedicated details view or use a prompt.
    // For now, let's just show a toast with basic info or alert.
    alert(`Ticket #${id}\nIssue: ${ticket.issue}\nStatus: ${ticket.status}\nRaised By: ${user ? user.name : 'Unknown'}\nAssigned To: ${exec ? exec.name : 'Unassigned'}`);
}

async function closeTicket(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/ticket-service/tickets/${id}`, {
            method: 'PATCH'
        });
        if (response.ok) {
            showToast('Ticket closed', 'success');
            loadTickets();
        }
    } catch (error) {
        showToast('Error closing ticket', 'error');
    }
}

async function deleteTicket(id) {
    if (!confirm('Are you sure you want to delete this ticket?')) return;
    try {
        const response = await fetch(`${API_BASE_URL}/ticket-service/tickets/${id}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            showToast('Ticket deleted', 'success');
            loadTickets();
        }
    } catch (error) {
        showToast('Error deleting ticket', 'error');
    }
}

// Helpers
function populateUserDropdowns() {
    const assignedToSelect = document.getElementById('ticketAssignedTo');
    
    const execsHtml = '<option value="">Unassigned</option>' + 
        users.filter(u => u.role === 'EXECUTIVE' || u.role === 'ADMIN')
             .map(u => `<option value="${u.id}">${u.name}</option>`).join('');
    assignedToSelect.innerHTML = execsHtml;
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
        <span>${message}</span>
    `;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Utils
function editUser(id) { openUserModal(id); }
function editTicket(id) { openTicketModal(id); }

async function updateDashboardStats() {
     await loadDashboardData();
}

function handleSearch(e) {
    const term = e.target.value.toLowerCase();
    const activePage = document.querySelector('.page.active').id;

    if (activePage === 'dashboardPage') {
        const filtered = tickets.filter(t => 
            t.issue.toLowerCase().includes(term) || 
            getUserName(t.raisedBy).toLowerCase().includes(term)
        );
        renderFilteredRecentTickets(filtered);
    } else if (activePage === 'usersPage') {
        const filtered = users.filter(u => 
            u.name.toLowerCase().includes(term) || 
            u.email.toLowerCase().includes(term) ||
            u.role.toLowerCase().includes(term)
        );
        renderFilteredUsers(filtered);
    } else if (activePage === 'ticketsPage') {
        const filtered = tickets.filter(t => 
            t.issue.toLowerCase().includes(term) || 
            getUserName(t.raisedBy).toLowerCase().includes(term) ||
            getUserName(t.assignedTo).toLowerCase().includes(term) ||
            t.status.toLowerCase().includes(term)
        );
        renderFilteredTickets(filtered);
    }
}

function renderFilteredRecentTickets(filtered) {
    const recent = filtered.slice(-5).reverse();
    recentTicketsBody.innerHTML = recent.map(ticket => `
        <tr>
            <td>#${ticket.id}</td>
            <td>${ticket.issue}</td>
            <td>${getUserName(ticket.raisedBy)}</td>
            <td><span class="status-pill ${ticket.status.toLowerCase()}">${ticket.status}</span></td>
            <td>${ticket.raisedOn}</td>
        </tr>
    `).join('');
}

function renderFilteredUsers(filtered) {
    usersBody.innerHTML = filtered.map(user => `
        <tr>
            <td>#${user.id}</td>
            <td><strong>${user.name}</strong></td>
            <td>${user.email}</td>
            <td>${user.phone}</td>
            <td><span class="status-pill assigned">${user.role}</span></td>
            <td class="action-btns">
                <button class="btn-icon" onclick="editUser(${user.id})"><i class="fas fa-edit"></i></button>
                <button class="btn-icon delete" onclick="deleteUser(${user.id})"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

function renderFilteredTickets(filtered) {
    ticketsBody.innerHTML = filtered.map(ticket => `
        <tr>
            <td>#${ticket.id}</td>
            <td>${ticket.issue}</td>
            <td>${getUserName(ticket.raisedBy)}</td>
            <td>${getUserName(ticket.assignedTo) || 'Unassigned'}</td>
            <td><span class="status-pill ${ticket.status.toLowerCase()}">${ticket.status}</span></td>
            <td class="action-btns">
                <button class="btn-icon" onclick="viewTicket(${ticket.id})" title="View Details"><i class="fas fa-eye"></i></button>
                <button class="btn-icon" onclick="editTicket(${ticket.id})" title="Edit"><i class="fas fa-edit"></i></button>
                ${ticket.status !== 'CLOSED' ? `<button class="btn-icon" onclick="closeTicket(${ticket.id})" title="Close Ticket"><i class="fas fa-check"></i></button>` : ''}
                <button class="btn-icon delete" onclick="deleteTicket(${ticket.id})" title="Delete"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `).join('');
}
