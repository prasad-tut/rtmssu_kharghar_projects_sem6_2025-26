// Admin Dashboard

let currentUser = null;
let otherPriceCount = 0;

// ------------------------
// Product Functions (defined early for global access)
// ------------------------

function showAddProductModal() {
    // reset
    document.getElementById('addProductForm').reset();
    otherPriceCount = 0;
    document.getElementById('otherPricesList').innerHTML = '';
    // add 1 field by default
    addOtherPriceField();
    document.getElementById('addProductModal').classList.add('active');
}

function addOtherPriceField(value = '') {
    if (otherPriceCount >= 5) {
        showToast('Max 5 price options allowed', 'info');
        return;
    }
    otherPriceCount += 1;
    const container = document.getElementById('otherPricesList');
    const id = `otherPrice_${otherPriceCount}`;
    const row = document.createElement('div');
    row.style.display = 'flex';
    row.style.gap = '0.5rem';
    row.innerHTML = `
        <input type="text" id="${id}" placeholder="Price option ${otherPriceCount}" value="${value.replace(/"/g, '&quot;')}" required style="flex:1;">
        <button class="btn btn-secondary btn-sm" type="button" title="Remove" onclick="removeOtherPriceField('${id}', this)">
            <i class="fas fa-times"></i>
        </button>
    `;
    container.appendChild(row);
}

function removeOtherPriceField(inputId, btn) {
    const input = document.getElementById(inputId);
    if (input && input.parentElement) {
        input.parentElement.remove();
        otherPriceCount = Math.max(0, otherPriceCount - 1);
    }
}

async function loadProducts() {
    Loading.show();

    const tbody = document.getElementById('productsTable');
    if (tbody) tbody.innerHTML = getSkeletonRowsHtml(7);
    try {
        // Fetch all products instead of only by adminId to ensure admin sees everything
        const products = await apiCall('/admin/products');
        const tbody = document.getElementById('productsTable');

        if (!products || products.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No products yet</td></tr>';
            return;
        }

        tbody.innerHTML = products.map(p => `
            <tr>
                <td><strong>#${p.id}</strong></td>
                <td>${escapeHtml(p.productType)}</td>
                <td>${escapeHtml(p.modelName)}</td>
                <td>${escapeHtml(p.mainColor)}</td>
                <td><strong>$${formatMoney(p.maxPrice)}</strong></td>
                <td>${formatDate(p.createdAt)}</td>
                <td>${p.aiOverviewJson ? '<span class="badge badge-resolved">Yes</span>' : '<span class="badge badge-pending">No</span>'}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading products:', error);
        const tbody = document.getElementById('productsTable');
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">
                <i class="fas fa-exclamation-circle"></i> Failed to load: ${escapeHtml(error.message || 'Unknown error')}
            </td></tr>`;
        }
    } finally {
        Loading.hide();
    }
}

async function submitProduct(useAi) {
    const btnManual = document.getElementById('btnPostManual');
    const btnAi = document.getElementById('btnPostAi');

    // validate required fields
    const productType = document.getElementById('productType').value.trim();
    const modelName = document.getElementById('modelName').value.trim();
    const mainColor = document.getElementById('mainColor').value.trim();
    const otherColorsRaw = document.getElementById('otherColors').value.trim();
    const maxPriceVal = document.getElementById('maxPrice').value;
    const imageUrl = document.getElementById('imageUrl').value.trim();
    const overview = document.getElementById('overview').value.trim();

    if (!productType || !modelName || !mainColor || !otherColorsRaw || !maxPriceVal || !imageUrl || !overview) {
        showToast('Please fill all fields', 'error');
        return;
    }

    const otherPrices = Array.from(document.querySelectorAll('#otherPricesList input'))
        .map(i => i.value.trim())
        .filter(Boolean);

    if (otherPrices.length === 0) {
        showToast('Please add at least one price option', 'error');
        return;
    }

    const payload = {
        adminId: currentUser.id,
        productType,
        modelName,
        mainColor,
        otherColors: otherColorsRaw.split(',').map(s => s.trim()).filter(Boolean),
        maxPrice: parseFloat(maxPriceVal),
        otherPrices,
        imageUrl,
        overview
    };

    const originalManual = btnManual.innerHTML;
    const originalAi = btnAi.innerHTML;
    Loading.show();
    btnManual.disabled = true;
    btnAi.disabled = true;

    if (useAi) {
        btnAi.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Generating...';
    } else {
        btnManual.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Posting...';
    }

    try {
        await apiCall(useAi ? '/admin/products/ai' : '/admin/products', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        showToast(useAi ? 'Product created with AI overview' : 'Product posted successfully', 'success');
        closeModal('addProductModal');
        loadProducts();
    } catch (error) {
        showToast(error.message || 'Failed to create product', 'error');
    } finally {
        Loading.hide();
        btnManual.disabled = false;
        btnAi.disabled = false;
        btnManual.innerHTML = originalManual;
        btnAi.innerHTML = originalAi;
    }
}

function formatMoney(val) {
    const num = Number(val || 0);
    return num.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 2 });
}

function escapeHtml(str) {
    return String(str ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    // CSS override
    const style = document.createElement('style');
    style.textContent = `
        .content-section { display: none !important; }
        .content-section.active { display: block !important; }
    `;
    document.head.appendChild(style);

    currentUser = checkAuth('ADMIN');
    if (!currentUser) return;

    initializeDashboard();
});

function initializeDashboard() {
    document.getElementById('userName').textContent = currentUser.name;
    document.getElementById('userAvatar').textContent = getInitials(currentUser.name);

    loadDashboard();
}

// Section Navigation
function showSection(section) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

    const sectionElement = document.getElementById(`${section}-section`);
    if (sectionElement) {
        sectionElement.classList.add('active');
    }

    if (section === 'dashboard') loadDashboard();
    if (section === 'tickets') loadAllTickets();
    if (section === 'products') loadProducts();
    if (section === 'users') loadUsers();
    if (section === 'executives') loadExecutives();
}

// Load Dashboard
async function loadDashboard() {
    try {
        const stats = await apiCall('/admin/dashboard');

        document.getElementById('totalTickets').textContent = stats.totalTickets || 0;
        document.getElementById('openTickets').textContent = stats.openTickets || 0;
        document.getElementById('resolvedTickets').textContent = stats.resolvedTickets || 0;
        document.getElementById('totalUsers').textContent = stats.totalUsers || 0;
        document.getElementById('totalExecutives').textContent = stats.totalExecutives || 0;

        // Load recent tickets
        loadRecentTickets();
        loadExecutiveWorkload();

    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

async function loadRecentTickets() {
    const tbody = document.getElementById('recentTicketsTable');
    if (tbody) tbody.innerHTML = getSkeletonRowsHtml(4, 3);
    try {
        const tickets = await apiCall('/admin/tickets');
        const tbody = document.getElementById('recentTicketsTable');

        if (!tickets || tickets.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No tickets</td></tr>';
            return;
        }

        tbody.innerHTML = tickets.slice(0, 5).map(ticket => `
            <tr>
                <td><strong>#${ticket.id}</strong></td>
                <td>${ticket.title}</td>
                <td><span class="badge badge-${getStatusClass(ticket.status)}">${formatStatus(ticket.status)}</span></td>
                <td><span class="badge badge-${(ticket.priority || 'medium').toLowerCase()}">${ticket.priority || '-'}</span></td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Error loading recent tickets:', error);
    }
}

async function loadExecutiveWorkload() {
    const tbody = document.getElementById('executiveWorkloadTable');
    if (tbody) tbody.innerHTML = getSkeletonRowsHtml(5, 3);
    try {
        const executives = await apiCall('/admin/executives/workload');
        const tbody = document.getElementById('executiveWorkloadTable');

        if (!executives || executives.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No executives</td></tr>';
            return;
        }

        tbody.innerHTML = executives.map(exec => `
            <tr>
                <td>${exec.executiveId}</td>
                <td>
                    ${exec.executiveName || exec.name}
                    <span class="status-dot ${exec.isOnline ? 'online' : 'offline'}" title="${exec.isOnline ? 'Online' : 'Offline'}"></span>
                </td>
                <td>${exec.totalAssigned || 0}</td>
                <td>${exec.openTickets || 0}</td>
                <td>${exec.resolvedTickets || 0}</td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Error loading workload:', error);
    }
}

// Load All Tickets
async function loadAllTickets() {
    const tbody = document.getElementById('allTicketsTable');
    if (tbody) tbody.innerHTML = getSkeletonRowsHtml(9);
    try {
        const tickets = await apiCall('/admin/tickets');
        const tbody = document.getElementById('allTicketsTable');

        if (!tickets || tickets.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No tickets found</td></tr>';
            return;
        }

        tbody.innerHTML = tickets.map(ticket => `
            <tr>
                <td><strong>#${ticket.id}</strong></td>
                <td>${ticket.title}</td>
                <td>${ticket.customerName || 'Customer #' + ticket.customerId}</td>
                <td>${ticket.category || '-'}</td>
                <td><span class="badge badge-${(ticket.priority || 'medium').toLowerCase()}">${ticket.priority || '-'}</span></td>
                <td><span class="badge badge-${getStatusClass(ticket.status)}">${formatStatus(ticket.status)}</span></td>
                <td>${ticket.executiveName || 'Unassigned'}</td>
                <td>${formatDate(ticket.createdAt)}</td>
                <td>
                    <div style="display: flex; gap: 0.3rem;">
                        ${!ticket.executiveId ? `
                            <button class="btn btn-sm btn-outline-primary" style="padding: 0.2rem 0.4rem; font-size: 0.7rem;" onclick="openAssignModal(${ticket.id}, '${ticket.title.replace(/'/g, "\\'").replace(/"/g, '&quot;')}')">
                                <i class="fas fa-user-plus"></i> Set
                            </button>
                            <button class="btn btn-sm btn-primary" style="padding: 0.2rem 0.4rem; font-size: 0.7rem;" onclick="autoAssignTicket(${ticket.id}, this)">
                                <i class="fas fa-magic"></i> Auto
                            </button>
                        ` : `
                            ${ticket.status !== 'CLOSED' && ticket.status !== 'RESOLVED' ? `
                                <button class="btn btn-sm btn-outline-info" style="padding: 0.2rem 0.4rem; font-size: 0.7rem;" onclick="openAssignModal(${ticket.id}, '${ticket.title.replace(/'/g, "\\'").replace(/"/g, '&quot;')}')" title="Move to other executive">
                                    <i class="fas fa-exchange-alt"></i> Move
                                </button>
                            ` : ''}
                            ${ticket.status !== 'CLOSED' && ticket.status !== 'RESOLVED' ? `
                                <button class="btn btn-sm btn-outline-success" style="padding: 0.2rem 0.4rem; font-size: 0.7rem;" onclick="closeTicket(${ticket.id}, this)">
                                    <i class="fas fa-check"></i> Close
                                </button>
                            ` : ''}
                            <button class="btn btn-sm btn-outline-danger" style="padding: 0.2rem 0.4rem; font-size: 0.7rem;" onclick="deleteTicket(${ticket.id}, this)">
                                <i class="fas fa-trash"></i> Del
                            </button>
                        `}
                    </div>
                </td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Error loading tickets:', error);
    }
}

// Load Users
async function loadUsers() {
    const tbody = document.getElementById('usersTable');
    if (tbody) tbody.innerHTML = getSkeletonRowsHtml(6);
    try {
        const users = await apiCall('/admin/users');
        const tbody = document.getElementById('usersTable');

        if (!users || users.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No users found</td></tr>';
            return;
        }

        // Sort: Role Priority (ADMIN > EXECUTIVE > CUSTOMER) then ID (ascending)
        const roleOrder = { 'ADMIN': 1, 'EXECUTIVE': 2, 'CUSTOMER': 3 };
        users.sort((a, b) => {
            const roleDiff = (roleOrder[a.role] || 4) - (roleOrder[b.role] || 4);
            if (roleDiff !== 0) return roleDiff;
            return a.id - b.id; // Secondary sort by ID ascending
        });

        tbody.innerHTML = users.map(user => `
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td><span class="badge badge-${user.role === 'ADMIN' ? 'danger' : user.role === 'EXECUTIVE' ? 'info' : 'success'}">${user.role}</span></td>
                <td><span class="badge badge-${user.active !== false ? 'success' : 'closed'}">${user.active !== false ? 'Active' : 'Inactive'}</span></td>
                <td>${formatDate(user.createdAt)}</td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Error loading users:', error);
    }
}

// Load Executives
async function loadExecutives() {
    const tbody = document.getElementById('executivesTable');
    if (tbody) tbody.innerHTML = getSkeletonRowsHtml(7);
    try {
        const executives = await apiCall('/admin/executives/workload');
        const tbody = document.getElementById('executivesTable');

        if (!executives || executives.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No executives found</td></tr>';
            return;
        }

        tbody.innerHTML = executives.map(exec => `
            <tr>
                <td>${exec.executiveId || exec.id}</td>
                <td>
                    ${exec.executiveName || exec.name}
                    <span class="status-dot ${exec.isOnline ? 'online' : 'offline'}" title="${exec.isOnline ? 'Online' : 'Offline'}"></span>
                </td>
                <td>${exec.email || '-'}</td>
                <td><strong>${exec.totalAssigned || 0}</strong></td>
                <td>${exec.openTickets || 0}</td>
                <td>${exec.inProgressTickets || 0}</td>
                <td><span class="text-success">${exec.resolvedTickets || 0}</span></td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Error loading executives:', error);
    }
}

// Modal functions
function showAddUserModal() {
    document.getElementById('addUserModal').classList.add('active');
}

function showAddExecutiveModal() {
    document.getElementById('newUserRole').value = 'EXECUTIVE';
    document.getElementById('addUserModal').classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

// Add User
async function addUser(event) {
    event.preventDefault();

    const userData = {
        name: document.getElementById('newUserName').value,
        email: document.getElementById('newUserEmail').value,
        password: document.getElementById('newUserPassword').value,
        role: document.getElementById('newUserRole').value,
        phone: document.getElementById('newUserPhone').value
    };

    Loading.show();
    try {
        await apiCall('/admin/users', {
            method: 'POST',
            body: JSON.stringify(userData)
        });

        showToast('User created successfully', 'success');
        closeModal('addUserModal');
        document.getElementById('addUserForm').reset();
        loadUsers();
        loadDashboard();

    } catch (error) {
        showToast(error.message || 'Failed to create user', 'error');
    } finally {
        Loading.hide();
    }
}


// Assignment functions
// Assignment functions
async function openAssignModal(ticketId, title) {
    document.getElementById('assignTicketId').value = ticketId;
    document.getElementById('assignTicketTitle').textContent = `Ticket #${ticketId}: ${title}`;

    try {
        const executives = await apiCall('/admin/executives/workload');
        const select = document.getElementById('executiveSelect');

        select.innerHTML = '<option value="">Choose an executive...</option>' +
            executives.map(exec => {
                const workload = exec.openTickets + exec.inProgressTickets;
                const status = exec.isOnline ? '🟢' : '⚪';
                return `<option value="${exec.executiveId}">${status} ${exec.executiveName} (${exec.email}) - ${workload} active tickets</option>`;
            }).join('');

        document.getElementById('assignModal').classList.add('active');
    } catch (error) {
        console.error('Error loading executives:', error);
        showToast('Failed to load executives', 'error');
    }
}

async function autoAssignTicket(ticketId, btnElement) {
    if (!confirm('Auto-assign this ticket to the executive with the least workload?')) return;

    const originalText = btnElement ? btnElement.innerHTML : '';
    if (btnElement) {
        btnElement.disabled = true;
        btnElement.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    }

    try {
        await apiCall(`/admin/executives/${ticketId}/auto-assign`, {
            method: 'POST'
        });

        showToast('Ticket auto-assigned successfully', 'success');
        loadAllTickets();
        loadDashboard();
    } catch (error) {
        showToast(error.message || 'Failed to auto-assign ticket', 'error');
        if (btnElement) {
            btnElement.disabled = false;
            btnElement.innerHTML = originalText;
        }
    }
}

async function closeTicket(ticketId, btn) {
    if (!confirm('Are you sure you want to close this ticket?')) return;
    try {
        await apiCall(`/admin/tickets/${ticketId}/close`, { method: 'POST' });
        showToast('Ticket closed successfully', 'success');
        loadAllTickets();
        loadDashboard();
    } catch (error) {
        showToast(error.message || 'Failed to close ticket', 'error');
    }
}

async function deleteTicket(ticketId, btn) {
    if (!confirm('PERMANENTLY delete this ticket? This cannot be undone.')) return;
    try {
        await apiCall(`/admin/tickets/${ticketId}`, { method: 'DELETE' });
        showToast('Ticket deleted', 'success');
        loadAllTickets();
        loadDashboard();
    } catch (error) {
        showToast(error.message || 'Failed to delete ticket', 'error');
    }
}

async function submitAssignment(event) {
    event.preventDefault();

    const ticketId = document.getElementById('assignTicketId').value;
    const executiveId = document.getElementById('executiveSelect').value;

    if (!executiveId) {
        showToast('Please select an executive', 'error');
        return;
    }

    try {
        await apiCall(`/admin/tickets/${ticketId}/assign`, {
            method: 'POST',
            body: JSON.stringify({ executiveId: parseInt(executiveId) })
        });

        showToast('Ticket assigned successfully', 'success');
        closeModal('assignModal');
        loadAllTickets();
        loadDashboard();
    } catch (error) {
        showToast(error.message || 'Failed to assign ticket', 'error');
    }
}

function getStatusClass(status) {
    const classes = {
        'OPEN': 'open',
        'PENDING': 'pending',
        'IN_PROGRESS': 'in-progress',
        'RESOLVED': 'resolved',
        'CLOSED': 'closed'
    };
    return classes[status] || 'open';
}

function formatStatus(status) {
    return status ? status.replace('_', ' ') : '-';
}

