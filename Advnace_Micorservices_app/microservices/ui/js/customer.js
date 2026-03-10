// Customer Dashboard

let currentUser = null;
let currentTicketId = null;
let products = [];
let pollingTimer = null;
let isAjaxLoading = false; // To prevent overlapping requests during polling

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOMContentLoaded fired');
    initializeApp();
});

if (document.readyState === 'interactive' || document.readyState === 'complete') {
    console.log('Document already ready, initializing...');
    initializeApp();
}

function initializeApp() {
    // Prevent multiple initializations
    if (window.appInitialized) return;
    window.appInitialized = true;

    // Override CSS for section display
    const style = document.createElement('style');
    style.textContent = `
        .content-section { display: none !important; }
        .content-section.active { display: block !important; }
    `;
    document.head.appendChild(style);

    currentUser = checkAuth('CUSTOMER');
    console.log('currentUser:', currentUser);
    if (!currentUser) {
        console.log('No user found, redirecting...');
        return;
    }

    initializeDashboard();
}

function initializeDashboard() {
    // Set user info
    document.getElementById('userName').textContent = currentUser.name;
    document.getElementById('userAvatar').textContent = getInitials(currentUser.name);
    document.getElementById('welcomeName').textContent = currentUser.name.split(' ')[0];

    // Load data
    loadDashboard();
    loadProducts();
}

// Section Navigation
function showSection(section) {
    // Hide all sections
    document.querySelectorAll('.content-section').forEach(s => {
        s.classList.remove('active');
    });
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

    // Clear existing polling
    if (pollingTimer) {
        clearInterval(pollingTimer);
        pollingTimer = null;
    }

    // Show selected section
    const sectionElement = document.getElementById(`${section}-section`);
    if (sectionElement) {
        sectionElement.classList.add('active');
    }

    // Update nav
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        if (item.textContent.toLowerCase().includes(section.replace('-', ' '))) {
            item.classList.add('active');
        }
    });

    // Load and Start Smart Polling
    if (section === 'dashboard') {
        loadDashboard();
        pollingTimer = setInterval(loadDashboard, 5000); // Refresh dashboard every 5s
    }
    if (section === 'products') {
        loadProducts();
    }
    if (section === 'ticket-detail') {
        // Ticket detail polling is started within viewTicket
    }
}

// Load Dashboard
async function loadDashboard() {
    if (isAjaxLoading) return; // Skip if a request is already in progress

    try {
        isAjaxLoading = true;
        const tickets = await apiCall(`/customer/tickets/my/${currentUser.id}`);

        // Update stats
        const total = tickets.length;
        const open = tickets.filter(t => t.status === 'OPEN').length;
        const inProgress = tickets.filter(t => t.status === 'IN_PROGRESS').length;
        const resolved = tickets.filter(t => t.status === 'RESOLVED' || t.status === 'CLOSED').length;

        document.getElementById('totalTickets').textContent = total;
        document.getElementById('openTickets').textContent = open;
        document.getElementById('inProgressTickets').textContent = inProgress;
        document.getElementById('resolvedTickets').textContent = resolved;

        // Render all tickets in dashboard
        renderTicketsTable('allTicketsTable', tickets, true);

    } catch (error) {
        console.error('Error loading dashboard:', error);
    } finally {
        isAjaxLoading = false;
    }
}



// Render Tickets Table
function renderTicketsTable(tableId, tickets, showActions) {
    const tbody = document.getElementById(tableId);

    if (!tickets || tickets.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="${showActions ? 7 : 5}" class="text-center text-muted" style="padding: 3rem;">
                    <i class="fas fa-ticket-alt" style="font-size: 2rem; opacity: 0.3; display: block; margin-bottom: 1rem;"></i>
                    No tickets found. <a href="#" onclick="showSection('new-ticket')">Create your first ticket</a>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = tickets.map(ticket => `
        <tr>
            <td data-label="ID"><strong>#${ticket.id}</strong></td>
            <td data-label="TITLE">${ticket.title}</td>
            <td data-label="CATEGORY">${ticket.category || '-'}</td>
            <td data-label="PRIORITY"><span class="badge badge-${(ticket.priority || 'medium').toLowerCase()}">${ticket.priority || '-'}</span></td>
            <td data-label="STATUS"><span class="badge badge-${getStatusClass(ticket.status)}">${formatStatus(ticket.status)}</span></td>
            ${showActions ? `<td data-label="ASSIGNED TO">${ticket.executiveName || 'Unassigned'}</td>` : ''}
            ${showActions ? `
                <td data-label="ACTIONS">
                    <button class="btn btn-sm btn-secondary" onclick="viewTicket(${ticket.id})">
                        <i class="fas fa-eye"></i> View
                    </button>
                </td>
            ` : ''}
        </tr>
    `).join('');
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

// Load Products (real-time from admin DB)
async function loadProducts() {
    console.log('🔄 Loading products...');

    // Use global loader
    Loading.show();

    const grid = document.getElementById('productGrid');
    if (grid) {
        grid.innerHTML = Array(4).fill(0).map(() => `
            <div class="product-card">
                <div class="product-image skeleton"></div>
                <div class="product-info">
                    <div class="skeleton-text skeleton" style="width: 40%;"></div>
                    <div class="skeleton-text skeleton" style="width: 80%; height: 1.2rem;"></div>
                    <div class="skeleton-text skeleton" style="width: 30%;"></div>
                </div>
            </div>
        `).join('');
    }

    try {
        const list = await apiCall('/admin/products/catalog');
        console.log('✅ Products API response:', list);
        console.log(`📦 Received ${list ? list.length : 0} products`);

        products = (list || []).map(p => ({
            id: p.id,
            name: p.modelName,
            category: p.productType,
            price: p.maxPrice,
            imageUrl: p.imageUrl
        }));

        console.log('📋 Mapped products:', products);
        renderProducts(products);

        // Populate product dropdown
        const select = document.getElementById('ticketProduct');
        if (select) {
            select.innerHTML = '<option value="">Select a product (optional)</option>' +
                products.map(p => `<option value="${p.id}">${escapeHtml(p.name)}</option>`).join('');
            console.log('✅ Product dropdown populated with', products.length, 'options');
        }
    } catch (error) {
        console.error('❌ Error loading products:', error);
        products = [];
        renderProducts(products);
    } finally {
        // Hide global loader
        Loading.hide();
    }
}

function renderProducts(productsList) {
    console.log('🎨 Rendering products...', productsList);
    const grid = document.getElementById('productGrid');
    if (!grid) {
        console.error('❌ CRITICAL: productGrid element not found in DOM');
        return;
    }
    console.log('✅ productGrid element found');

    // Clear existing content
    grid.innerHTML = '';

    if (!productsList || productsList.length === 0) {
        console.log('⚠️ No products to display');
        grid.innerHTML = `
            <div style="grid-column: 1/-1; text-align: center; padding: 2rem; color: #64748b;">
                <i class="fas fa-box-open" style="font-size: 3rem; margin-bottom: 1rem;"></i>
                <p>No products available at the moment.</p>
            </div>
        `;
        return;
    }

    const html = productsList.map(product => `
        <div class="product-card">
            <div class="product-image">
                ${product.imageUrl ? `
                    <img src="${escapeHtml(product.imageUrl)}" alt="${escapeHtml(product.name)}" style="width:100%;height:100%;object-fit:cover;border-radius:12px;" onerror="this.style.display='none';this.parentElement.innerHTML='<i class=\\'fas fa-box\\'></i>';">
                ` : `<i class="fas fa-box"></i>`}
            </div>
            <div class="product-info">
                <span class="product-category">${product.category || 'General'}</span>
                <h3 class="product-name">${product.name || 'Unnamed Product'}</h3>
                <span class="product-price">$${(product.price || 0).toLocaleString()}</span>
            </div>
            <div class="product-actions">
                <button class="btn btn-primary btn-sm" onclick="raiseTicketForProduct(${product.id})" style="width: 100%;">
                    <i class="fas fa-headset"></i> Get Support
                </button>
            </div>
        </div>
    `).join('');

    grid.innerHTML = html;
    console.log('✅ Successfully rendered', productsList.length, 'products');
}



function filterProducts() {
    const search = document.getElementById('productSearch').value.toLowerCase();
    const filtered = (products || []).filter(p =>
        (p.name || '').toLowerCase().includes(search) ||
        (p.category || '').toLowerCase().includes(search)
    );
    renderProducts(filtered);
}

function raiseTicketForProduct(productId) {
    const product = (products || []).find(p => p.id === productId);
    if (product) {
        document.getElementById('ticketProduct').value = productId;
        document.getElementById('ticketTitle').value = `Issue with ${product.name}`;
        showSection('new-ticket');
    }
}

function escapeHtml(str) {
    return String(str ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

// Create Ticket
async function createTicket(event) {
    event.preventDefault();

    const ticketData = {
        customerId: currentUser.id,
        title: document.getElementById('ticketTitle').value,
        description: document.getElementById('ticketDescription').value,
        category: document.getElementById('ticketCategory').value,
        priority: document.getElementById('ticketPriority').value
    };

    // Only add productId if selected
    const productVal = document.getElementById('ticketProduct').value;
    if (productVal && productVal !== '') {
        ticketData.productId = parseInt(productVal);
    }

    console.log('Submitting ticket:', JSON.stringify(ticketData));

    const btn = event.target.querySelector('button[type="submit"]');
    Loading.show();
    btn.disabled = true;

    try {
        const ticket = await apiCall('/customer/tickets', {
            method: 'POST',
            body: JSON.stringify(ticketData)
        });

        console.log('Ticket created:', ticket);
        showToast('Ticket created successfully!', 'success');
        document.getElementById('newTicketForm').reset();
        showSection('dashboard');

    } catch (error) {
        console.error('Ticket creation error:', error);
        showToast(error.message || 'Failed to create ticket', 'error');
    } finally {
        Loading.hide();
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-paper-plane"></i> Submit Ticket';
    }
}

// View Ticket Detail
async function viewTicket(ticketId) {
    currentTicketId = ticketId;

    try {
        const ticket = await apiCall(`/customer/tickets/${ticketId}`);
        window.currentTicketStatus = ticket.status; // Store globally for resolution-flow

        document.getElementById('ticketDetailId').textContent = ticket.id;
        document.getElementById('ticketDetailTitle').textContent = ticket.title;
        document.getElementById('ticketDetailStatus').innerHTML = `<span class="badge badge-${getStatusClass(ticket.status)}">${formatStatus(ticket.status)}</span>`;
        document.getElementById('ticketDetailPriority').innerHTML = `<span class="badge badge-${(ticket.priority || 'medium').toLowerCase()}">${ticket.priority || '-'}</span>`;
        document.getElementById('ticketDetailCategory').textContent = ticket.category || '-';
        document.getElementById('ticketDetailAssignee').textContent = ticket.executiveName || 'Unassigned';
        document.getElementById('ticketDetailCreated').textContent = formatDate(ticket.createdAt);
        document.getElementById('ticketDetailDescription').textContent = ticket.description || '-';

        // Disable chat input if already resolved or closed
        const isResolved = ticket.status === 'RESOLVED' || ticket.status === 'CLOSED';
        const messageInput = document.getElementById('messageInput');
        const chatInput = document.querySelector('.chat-input');

        if (messageInput) {
            messageInput.disabled = isResolved;
            messageInput.placeholder = isResolved ? 'This ticket has been resolved' : 'Type your message...';
        }
        if (chatInput) {
            chatInput.style.opacity = isResolved ? '0.5' : '1';
            chatInput.style.pointerEvents = isResolved ? 'none' : 'auto';
        }

        loadMessages(ticketId);
        showSection('ticket-detail');

        // Start live chat updates
        if (!isResolved) {
            pollingTimer = setInterval(() => loadMessages(ticketId), 3000); // Refresh chat every 3s
        }

    } catch (error) {
        showToast('Failed to load ticket details', 'error');
    }
}

// Load Messages
async function loadMessages(ticketId) {
    if (isAjaxLoading) return;

    try {
        isAjaxLoading = true;
        const messages = await apiCall(`/customer/messages/ticket/${ticketId}`);
        // Use the resolution-aware rendering function
        if (typeof renderMessagesWithResolution === 'function') {
            renderMessagesWithResolution(messages, window.currentTicketStatus);
        } else {
            renderMessages(messages);
        }
    } catch (error) {
        console.error('Error loading messages:', error);
    } finally {
        isAjaxLoading = false;
    }
}

function renderMessages(messages) {
    const container = document.getElementById('chatMessages');

    if (!messages || messages.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-comments"></i>
                <p>No messages yet. Start the conversation!</p>
            </div>
        `;
        return;
    }

    container.innerHTML = messages.map(msg => {
        const isMe = msg.senderId === currentUser.id;
        const displayName = isMe ? 'You' : (msg.senderName || 'Support Executive');
        return `
            <div class="message-wrapper ${isMe ? 'sent' : 'received'}">
                <div class="message-sender">
                    <i class="fas ${isMe ? 'fa-user' : 'fa-headset'}"></i>
                    ${displayName}
                </div>
                <div class="message-bubble">
                    ${msg.content}
                    <span class="message-time">${formatDate(msg.createdAt)}</span>
                </div>
            </div>
        `;
    }).join('');

    container.scrollTop = container.scrollHeight;
}

async function sendMessage() {
    const input = document.getElementById('messageInput');
    const content = input.value.trim();

    if (!content || !currentTicketId) return;

    try {
        await apiCall(`/customer/messages`, {
            method: 'POST',
            body: JSON.stringify({
                ticketId: currentTicketId,
                senderId: currentUser.id,
                senderType: 'CUSTOMER',
                content: content,
                messageType: 'REGULAR'
            })
        });

        input.value = '';
        selectedAttachment = null;
        document.getElementById('messageInput').placeholder = 'Type your message...';
        loadMessages(currentTicketId);

    } catch (error) {
        showToast('Failed to send message', 'error');
    }
}

let selectedAttachment = null;

async function selectImage() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.onchange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            if (file.size > 2 * 1024 * 1024) {
                showToast('Image too large. Max 2MB.', 'error');
                return;
            }
            const reader = new FileReader();
            reader.onload = (readerEvent) => {
                selectedAttachment = readerEvent.target.result;
                showToast('Image attached', 'success');
                document.getElementById('messageInput').placeholder = 'Image attached. Type message...';
            };
            reader.readAsDataURL(file);
        }
    };
    input.click();
}

// Enter key to send message
document.addEventListener('keypress', (e) => {
    if (e.key === 'Enter' && e.target.id === 'messageInput') {
        sendMessage();
    }
});
