// Executive Dashboard

let currentUser = null;
let currentTicketId = null;
let pollingTimer = null;
let isAjaxLoading = false; // To prevent overlapping requests during polling

// Products are loaded from admin-service (real-time) for product overview.

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    // CSS override for sections
    const style = document.createElement('style');
    style.textContent = `
        .content-section { display: none !important; }
        .content-section.active { display: block !important; }
    `;
    document.head.appendChild(style);

    currentUser = checkAuth('EXECUTIVE');
    if (!currentUser) return;

    initializeDashboard();
});

function initializeDashboard() {
    document.getElementById('userName').textContent = currentUser.name;
    document.getElementById('userAvatar').textContent = getInitials(currentUser.name);
    document.getElementById('welcomeName').textContent = currentUser.name.split(' ')[0];

    loadDashboard();
    pollingTimer = setInterval(loadDashboard, 5000); // Unified polling starts on dashboard
}

// Section Navigation
function showSection(section) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

    // Clear existing polling
    if (pollingTimer) {
        clearInterval(pollingTimer);
        pollingTimer = null;
    }

    const sectionElement = document.getElementById(`${section}-section`);
    if (sectionElement) {
        sectionElement.classList.add('active');
    }

    if (section === 'dashboard') {
        loadDashboard();
        pollingTimer = setInterval(loadDashboard, 5000);
    }
    if (section === 'my-tickets') loadMyTickets();
    if (section === 'open-tickets') loadOpenTickets();
    if (section === 'resolved') loadResolvedTickets();
}

// Load Dashboard
async function loadDashboard() {
    if (isAjaxLoading) return;
    try {
        isAjaxLoading = true;
        const workload = await apiCall(`/executive/${currentUser.id}/workload`);

        document.getElementById('totalAssigned').textContent = workload.totalAssigned || 0;
        document.getElementById('openTickets').textContent = workload.openTickets || 0;
        document.getElementById('inProgressTickets').textContent = workload.inProgressTickets || 0;
        document.getElementById('resolvedTickets').textContent = workload.resolvedTickets || 0;

        // Badge
        const badge = document.getElementById('ticketBadge');
        const openCount = workload.openTickets || 0;
        if (openCount > 0) {
            badge.textContent = openCount;
            badge.style.display = 'inline';
        } else {
            badge.style.display = 'none';
        }

        // Load urgent tickets
        const tickets = await apiCall(`/executive/${currentUser.id}/tickets`);
        const urgentTickets = tickets.filter(t =>
            (t.status === 'OPEN' || t.status === 'IN_PROGRESS' || t.priority === 'HIGH') &&
            t.status !== 'RESOLVED' &&
            t.status !== 'CLOSED'
        ).slice(0, 5);

        renderUrgentTickets(urgentTickets);

    } catch (error) {
        console.error('Error loading dashboard:', error);
    } finally {
        isAjaxLoading = false;
    }
}

function renderUrgentTickets(tickets) {
    const tbody = document.getElementById('urgentTicketsTable');

    if (!tickets || tickets.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted" style="padding: 3rem;">
                    <i class="fas fa-check-circle" style="font-size: 2rem; opacity: 0.3; display: block; margin-bottom: 1rem;"></i>
                    No urgent tickets! Great job!
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = tickets.map(ticket => `
        <tr>
            <td><strong>#${ticket.id}</strong></td>
            <td>${ticket.title}</td>
            <td>${ticket.customerName || 'Customer #' + ticket.customerId}</td>
            <td><span class="badge badge-${(ticket.priority || 'medium').toLowerCase()}">${ticket.priority || '-'}</span></td>
            <td><span class="badge badge-${getStatusClass(ticket.status)}">${formatStatus(ticket.status)}</span></td>
            <td>${formatDate(ticket.createdAt)}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="viewTicket(${ticket.id})">
                    <i class="fas fa-eye"></i> View
                </button>
            </td>
        </tr>
    `).join('');
}

// Load My Tickets
async function loadMyTickets() {
    const tbody = document.getElementById('myTicketsTable');
    if (tbody) tbody.innerHTML = getSkeletonRowsHtml(8);
    try {
        const tickets = await apiCall(`/executive/${currentUser.id}/tickets`);
        renderTicketsTable('myTicketsTable', tickets, true);
    } catch (error) {
        console.error('Error loading tickets:', error);
    }
}

// Load Open Tickets
async function loadOpenTickets() {
    const tbody = document.getElementById('openTicketsTable');
    if (tbody) tbody.innerHTML = getSkeletonRowsHtml(7);
    try {
        const tickets = await apiCall(`/executive/${currentUser.id}/tickets/open`);
        renderOpenTicketsTable('openTicketsTable', tickets);
    } catch (error) {
        console.error('Error loading open tickets:', error);
    }
}

// Load Resolved Tickets
async function loadResolvedTickets() {
    const tbody = document.getElementById('resolvedTicketsTable');
    if (tbody) tbody.innerHTML = getSkeletonRowsHtml(6);
    try {
        const tickets = await apiCall(`/executive/${currentUser.id}/tickets/resolved`);
        renderResolvedTable('resolvedTicketsTable', tickets);
    } catch (error) {
        console.error('Error loading resolved tickets:', error);
    }
}

function renderTicketsTable(tableId, tickets, showActions) {
    const tbody = document.getElementById(tableId);

    if (!tickets || tickets.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted" style="padding: 3rem;">
                    <i class="fas fa-inbox" style="font-size: 2rem; opacity: 0.3; display: block; margin-bottom: 1rem;"></i>
                    No tickets found
                </td>
            </tr>
        `;
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
            <td>${formatDate(ticket.createdAt)}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="viewTicket(${ticket.id})">
                    <i class="fas fa-eye"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function renderOpenTicketsTable(tableId, tickets) {
    const tbody = document.getElementById(tableId);

    if (!tickets || tickets.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted" style="padding: 3rem;">
                    No open tickets
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = tickets.map(ticket => `
        <tr>
            <td><strong>#${ticket.id}</strong></td>
            <td>${ticket.title}</td>
            <td>${ticket.customerName || 'Customer #' + ticket.customerId}</td>
            <td>${ticket.category || '-'}</td>
            <td><span class="badge badge-${(ticket.priority || 'medium').toLowerCase()}">${ticket.priority || '-'}</span></td>
            <td>${formatDate(ticket.createdAt)}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="viewTicket(${ticket.id})">
                    <i class="fas fa-play"></i> Start
                </button>
            </td>
        </tr>
    `).join('');
}

function renderResolvedTable(tableId, tickets) {
    const tbody = document.getElementById(tableId);

    if (!tickets || tickets.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-muted" style="padding: 3rem;">
                    No resolved tickets yet
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = tickets.map(ticket => `
        <tr>
            <td><strong>#${ticket.id}</strong></td>
            <td>${ticket.title}</td>
            <td>${ticket.customerName || 'Customer #' + ticket.customerId}</td>
            <td>${ticket.category || '-'}</td>
            <td><span class="badge badge-${(ticket.priority || 'medium').toLowerCase()}">${ticket.priority || '-'}</span></td>
            <td>${formatDate(ticket.resolvedAt || ticket.updatedAt)}</td>
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

// View Ticket Detail
async function viewTicket(ticketId) {
    if (!ticketId) return;
    currentTicketId = ticketId;

    try {
        const ticket = await apiCall(`/executive/tickets/${ticketId}`);
        const messagesData = await apiCall(`/executive/tickets/${ticketId}/messages`);
        const messages = Array.isArray(messagesData) ? messagesData : [];

        window.currentTicketStatus = ticket.status;

        // Update UI Text
        document.getElementById('ticketDetailId').textContent = ticket.id;
        document.getElementById('ticketDetailTitle').textContent = ticket.title;
        document.getElementById('ticketDetailStatus').innerHTML = `<span class="badge badge-${getStatusClass(ticket.status)}">${formatStatus(ticket.status)}</span>`;
        document.getElementById('ticketDetailPriority').innerHTML = `<span class="badge badge-${(ticket.priority || 'medium').toLowerCase()}">${ticket.priority || '-'}</span>`;
        document.getElementById('ticketDetailCategory').textContent = ticket.category || '-';
        document.getElementById('ticketDetailCustomer').textContent = ticket.customerName || 'Customer #' + ticket.customerId;
        document.getElementById('ticketDetailCreated').textContent = formatDate(ticket.createdAt);
        document.getElementById('ticketDetailDescription').textContent = ticket.description || '-';

        const isResolved = ticket.status === 'RESOLVED' || ticket.status === 'CLOSED';
        const executiveHasReplied = messages.some(msg => msg.senderType === 'EXECUTIVE');

        // Toggle UI Elements
        const btnStart = document.getElementById('btnStartWork');
        const btnResolve = document.getElementById('btnResolve');
        const btnAskResolve = document.getElementById('btnAskResolve');
        const btnProductOverview = document.getElementById('btnProductOverview');
        const messageInput = document.getElementById('messageInput');
        const chatInput = document.querySelector('.chat-input');

        if (btnStart) btnStart.style.display = 'none';

        if (btnResolve) {
            btnResolve.style.display = (executiveHasReplied && !isResolved) ? 'inline-flex' : 'none';
        }

        if (btnAskResolve) btnAskResolve.style.display = isResolved ? 'none' : 'inline-flex';
        if (btnProductOverview) btnProductOverview.style.display = isResolved ? 'none' : 'inline-flex';

        if (messageInput) {
            messageInput.disabled = isResolved;
            messageInput.placeholder = isResolved ? 'This ticket is resolved' : 'Type your response...';
        }
        if (chatInput) {
            chatInput.style.opacity = isResolved ? '0.5' : '1';
            chatInput.style.pointerEvents = isResolved ? 'none' : 'auto';
        }

        // Render Chat
        if (typeof renderMessagesWithResolution === 'function') {
            renderMessagesWithResolution(messages, ticket.status);
        } else if (typeof renderMessages === 'function') {
            renderMessages(messages);
        }

        // Navigation and Polling
        const section = document.getElementById('ticket-detail-section');
        if (section && !section.classList.contains('active')) {
            showSection('ticket-detail');
        }

        // Dedicated Live Polling (keeps active to see ratings/feedback in real-time)
        if (pollingTimer) {
            clearInterval(pollingTimer);
        }
        pollingTimer = setInterval(() => loadMessages(ticketId), 3000);

        // Handle Rating Display
        if (isResolved && typeof checkAndDisplayRating === 'function') {
            setTimeout(checkAndDisplayRating, 100);
        }

    } catch (error) {
        console.error('View Ticket Error:', error);
        showToast('Error loading ticket details. Please try again.', 'error');
    }
}

// Load Messages
async function loadMessages(ticketId) {
    if (isAjaxLoading || !ticketId) return;
    try {
        isAjaxLoading = true;

        // Fetch latest status and messages
        const ticket = await apiCall(`/executive/tickets/${ticketId}`);
        const messagesData = await apiCall(`/executive/tickets/${ticketId}/messages`);
        const messages = Array.isArray(messagesData) ? messagesData : [];

        const wasResolved = window.currentTicketStatus === 'RESOLVED' || window.currentTicketStatus === 'CLOSED';
        const isNowResolved = ticket.status === 'RESOLVED' || ticket.status === 'CLOSED';

        window.currentTicketStatus = ticket.status;

        // If ticket state transitioned to resolved
        if (isNowResolved && !wasResolved) {
            // Just refresh the view to toggle buttons but KEEP POLLING for ratings
            viewTicket(ticketId);
            return;
        }

        // Update Chat UI
        if (typeof renderMessagesWithResolution === 'function') {
            renderMessagesWithResolution(messages, ticket.status);
        } else {
            renderMessages(messages);
        }

        // Ensure Feedback card is updated in real-time
        if (isNowResolved && typeof checkAndDisplayRating === 'function') {
            checkAndDisplayRating();
        }

    } catch (error) {
        console.error('Polling Error:', error);
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
                <p>No messages yet</p>
            </div>
        `;
        return;
    }

    container.innerHTML = messages.map(msg => {
        const isMe = msg.senderId === currentUser.id;
        const displayName = isMe ? 'You' : (msg.senderName || 'Customer');
        return `
            <div class="message-wrapper ${isMe ? 'sent' : 'received'}">
                <div class="message-sender">
                    <i class="fas ${isMe ? 'fa-headset' : 'fa-user'}"></i>
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

async function sendMessage() {
    const input = document.getElementById('messageInput');
    const content = input.value.trim();

    if (!content || !currentTicketId) return;

    try {
        await apiCall(`/executive/tickets/${currentTicketId}/messages`, {
            method: 'POST',
            body: JSON.stringify({
                ticketId: currentTicketId,
                senderId: currentUser.id,
                senderType: 'EXECUTIVE',
                content: content,
                messageType: 'REGULAR'
            })
        });

        input.value = '';

        // Reload messages and update button visibility
        const messages = await apiCall(`/executive/tickets/${currentTicketId}/messages`);
        renderMessages(messages);

        // Show Resolve button after first executive message
        const executiveHasReplied = messages.some(msg => msg.senderType === 'EXECUTIVE');
        const btnResolve = document.getElementById('btnResolve');
        const ticket = await apiCall(`/executive/tickets/${currentTicketId}`);

        if (executiveHasReplied && ticket.status !== 'RESOLVED' && ticket.status !== 'CLOSED') {
            btnResolve.style.display = 'inline-flex';
        }

    } catch (error) {
        showToast('Failed to send message', 'error');
    }
}

// ------------------------
// AI reply suggestions
// ------------------------

async function openAiSuggestions() {
    if (!currentTicketId) {
        showToast('Open a ticket first', 'info');
        return;
    }

    const modal = document.getElementById('aiSuggestModal');
    const content = document.getElementById('aiSuggestContent');
    if (modal) modal.classList.add('active');
    if (content) {
        content.innerHTML = `
            <div style="text-align: center; padding: 2rem;">
                <i class="fas fa-spinner fa-spin" style="font-size: 2rem; color: var(--primary);"></i>
                <p style="margin-top: 1rem; color: var(--gray-600);">Generating suggestions...</p>
            </div>
        `;
    }

    try {
        const res = await apiCall(`/executive/ai/tickets/${currentTicketId}/reply-suggestions`);
        const suggestions = (res && res.suggestions) ? res.suggestions : [];
        renderAiSuggestions(suggestions);
    } catch (e) {
        if (content) {
            content.innerHTML = `
                <div style="text-align: center; padding: 2rem;">
                    <i class="fas fa-exclamation-circle" style="font-size: 2rem; color: var(--danger);"></i>
                    <p style="margin-top: 1rem; color: var(--gray-600);">Failed to generate suggestions</p>
                </div>
            `;
        }
    }
}

function renderAiSuggestions(suggestions) {
    const content = document.getElementById('aiSuggestContent');
    if (!content) return;

    if (!suggestions || suggestions.length === 0) {
        content.innerHTML = `<div class="text-muted">No suggestions available.</div>`;
        return;
    }

    content.innerHTML = `
        <div style="display:grid; gap: 0.75rem;">
            ${suggestions.map((s, idx) => `
                <div style="background: var(--gray-50); border: 1px solid var(--gray-200); border-radius: 12px; padding: 1rem;">
                    <div style="display:flex; justify-content: space-between; gap: 1rem; align-items: start;">
                        <div style="white-space: pre-wrap; color: var(--gray-800); flex: 1;">${escapeHtml(s)}</div>
                        <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                            <button class="btn btn-primary btn-sm" onclick="useSuggestion(${idx})">
                                Use
                            </button>
                            <button class="btn btn-secondary btn-sm" onclick="saveToDraft(${idx})">
                                Save to Draft
                            </button>
                        </div>
                    </div>
                </div>
            `).join('')}
            <div class="text-muted" style="font-size:0.85rem;">
                Tip: Click "Use" to copy to input, or "Save to Draft" to store it for later.
            </div>
        </div>
    `;

    window.__aiSuggestions = suggestions;
}

function useSuggestion(idx) {
    const suggestions = window.__aiSuggestions || [];
    const text = suggestions[idx];
    if (!text) return;
    const input = document.getElementById('messageInput');
    if (input) {
        input.value = text;
        input.focus();
        showToast('Suggestion copied to input', 'success');
    }
    // Note: Modal stays open as per user request
}

async function saveToDraft(idx) {
    const suggestions = window.__aiSuggestions || [];
    const text = suggestions[idx];
    if (!text || !currentTicketId) return;

    try {
        await apiCall('/executive/drafts', {
            method: 'POST',
            body: JSON.stringify({
                ticketId: currentTicketId,
                executiveId: currentUser.id,
                content: text
            })
        });
        showToast('Saved to drafts', 'success');
    } catch (error) {
        showToast('Failed to save draft', 'error');
    }
}

function closeAiSuggestions() {
    const modal = document.getElementById('aiSuggestModal');
    if (modal) modal.classList.remove('active');
}

// ------------------------
// Draft Management
// ------------------------

async function openDraftsModal() {
    if (!currentTicketId) {
        showToast('Open a ticket first', 'info');
        return;
    }

    const modal = document.getElementById('draftsModal');
    const content = document.getElementById('draftsContent');
    if (modal) modal.classList.add('active');

    if (content) {
        content.innerHTML = `
            <div style="text-align: center; padding: 2rem;">
                <i class="fas fa-spinner fa-spin" style="font-size: 2rem; color: var(--primary);"></i>
                <p style="margin-top: 1rem; color: var(--gray-600);">Loading drafts...</p>
            </div>
        `;
    }

    try {
        const drafts = await apiCall(`/executive/drafts/ticket/${currentTicketId}`);
        renderDraftList(drafts);
    } catch (e) {
        if (content) {
            content.innerHTML = `
                <div style="text-align: center; padding: 2rem;">
                    <i class="fas fa-exclamation-circle" style="font-size: 2rem; color: var(--danger);"></i>
                    <p style="margin-top: 1rem; color: var(--gray-600);">Failed to load drafts</p>
                </div>
            `;
        }
    }
}

function renderDraftList(drafts) {
    const content = document.getElementById('draftsContent');
    if (!content) return;

    if (!drafts || drafts.length === 0) {
        content.innerHTML = `
            <div style="text-align: center; padding: 2rem;">
                <i class="fas fa-file-signature" style="font-size: 3rem; color: var(--gray-300); display: block; margin-bottom: 1rem;"></i>
                <p style="color: var(--gray-500);">No saved drafts for this ticket.</p>
            </div>
        `;
        return;
    }

    content.innerHTML = `
        <div style="display:grid; gap: 1rem;">
            ${drafts.map(draft => `
                <div style="background: var(--gray-50); border: 1px solid var(--gray-200); border-radius: 12px; padding: 1.25rem;">
                    <div style="white-space: pre-wrap; color: var(--gray-800); margin-bottom: 1rem; font-size: 0.95rem;">${escapeHtml(draft.content)}</div>
                    <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--gray-100); pt: 0.75rem; padding-top: 0.75rem;">
                        <span class="text-muted" style="font-size: 0.8rem;">Saved ${formatDate(draft.createdAt)}</span>
                        <div style="display: flex; gap: 0.5rem;">
                            <button class="btn btn-danger btn-sm" onclick="deleteDraft(${draft.id})">
                                <i class="fas fa-trash"></i>
                            </button>
                            <button class="btn btn-primary btn-sm" onclick="useDraft(${draft.id}, \`${escapeHtml(draft.content).replace(/`/g, '\\`')}\`)">
                                Use Draft
                            </button>
                        </div>
                    </div>
                </div>
            `).join('')}
        </div>
    `;
}

async function deleteDraft(draftId) {
    if (!confirm('Are you sure you want to delete this draft?')) return;

    try {
        await apiCall(`/executive/drafts/${draftId}`, { method: 'DELETE' });
        showToast('Draft deleted', 'success');
        openDraftsModal(); // Refresh
    } catch (error) {
        showToast('Failed to delete draft', 'error');
    }
}

function useDraft(draftId, content) {
    const input = document.getElementById('messageInput');
    if (input) {
        input.value = content;
        input.focus();
        showToast('Draft loaded into input', 'success');
    }
    closeDraftsModal();
}

function closeDraftsModal() {
    const modal = document.getElementById('draftsModal');
    if (modal) modal.classList.remove('active');
}

// Start Work on Ticket
async function startWork() {
    if (!currentTicketId) return;
    Loading.show();

    try {
        await apiCall(`/executive/tickets/${currentTicketId}/start`, {
            method: 'POST'
        });

        showToast('Started working on ticket', 'success');
        viewTicket(currentTicketId);

    } catch (error) {
        showToast('Failed to start work', 'error');
    } finally {
        Loading.hide();
    }
}

// Resolve Ticket
async function resolveTicket() {
    if (!currentTicketId) return;
    Loading.show();

    try {
        await apiCall(`/executive/tickets/${currentTicketId}/resolve`, {
            method: 'POST'
        });

        showToast('Ticket resolved successfully', 'success');
        viewTicket(currentTicketId);


    } catch (error) {
        showToast('Failed to resolve ticket', 'error');
    } finally {
        Loading.hide();
    }
}

// Product Overview Modal
let currentProductId = null;

async function showProductOverview() {
    try {
        // Get current ticket to find product ID
        if (!currentTicketId) {
            showToast('No ticket selected', 'error');
            return;
        }

        const ticket = await apiCall(`/executive/tickets/${currentTicketId}`);

        if (!ticket.productId) {
            showToast('No product associated with this ticket', 'info');
            return;
        }

        currentProductId = ticket.productId;

        // Show modal by adding 'active' class
        const modal = document.getElementById('productModal');
        if (modal) {
            modal.classList.add('active');
        }

        // Fetch product from backend (admin products API via gateway)
        const product = await apiCall(`/admin/products/${currentProductId}`);
        if (!product) {
            document.getElementById('productContent').innerHTML = `
                <div style="text-align: center; padding: 2rem;">
                    <i class="fas fa-box-open" style="font-size: 3rem; color: var(--gray-400);"></i>
                    <p style="margin-top: 1rem; color: var(--gray-600);">Product details not available</p>
                </div>
            `;
            return;
        }

        renderProductDetails(product);

    } catch (error) {
        console.error('Error loading product:', error);
        document.getElementById('productContent').innerHTML = `
            <div style="text-align: center; padding: 2rem;">
                <i class="fas fa-exclamation-circle" style="font-size: 3rem; color: var(--danger);"></i>
                <p style="margin-top: 1rem; color: var(--gray-600);">Failed to load product details</p>
            </div>
        `;
    }
}

function renderProductDetails(product) {
    const headerName = product.modelName || product.name || 'Product';
    const headerCategory = product.productType || product.category || '';
    const price = product.maxPrice ?? product.price;
    const image = product.imageUrl || product.image_url || 'https://via.placeholder.com/200';

    let overviewHtml = '';
    const ai = product.aiOverviewJson;
    if (ai && String(ai).trim().length > 0) {
        const parsed = safeJsonParse(ai);
        if (parsed) {
            overviewHtml = renderAiOverview(parsed);
        } else {
            overviewHtml = `
                <div style="background: var(--gray-50); border-radius: 12px; padding: 1.25rem;">
                    <h3 style="margin: 0 0 0.75rem 0; font-size: 1.125rem; color: var(--gray-900);">
                        <i class="fas fa-robot" style="color: var(--primary); margin-right: 0.5rem;"></i>
                        AI Overview (raw)
                    </h3>
                    <pre style="white-space: pre-wrap; font-size: 0.9rem; color: var(--gray-700); margin:0;">${escapeHtml(ai)}</pre>
                </div>
            `;
        }
    } else {
        overviewHtml = `
            <div style="background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%); border-radius: 16px; padding: 1.75rem; box-shadow: 0 4px 12px rgba(0,0,0,0.08); border: 1px solid rgba(0,0,0,0.06);">
                <h3 style="margin: 0 0 1.25rem 0; font-size: 1.25rem; color: var(--gray-900); display: flex; align-items: center; gap: 0.75rem; padding-bottom: 1rem; border-bottom: 2px solid var(--primary-light);">
                    <i class="fas fa-info-circle" style="color: var(--primary); font-size: 1.1rem;"></i>
                    Product Overview (Admin)
                </h3>
                <div style="color: var(--gray-700); line-height: 1.8; font-size: 1rem;">${escapeHtml(product.manualOverview || 'No additional overview provided')}</div>
            </div>
        `;
    }

    // Always show Other Colors and Other Prices if they exist
    let extraDetailsHtml = '';
    if ((Array.isArray(product.otherColors) && product.otherColors.length > 0) ||
        (Array.isArray(product.otherPrices) && product.otherPrices.length > 0)) {

        extraDetailsHtml = `
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem; margin-top: 1.5rem;">
                ${Array.isArray(product.otherColors) && product.otherColors.length > 0 ? `
                    <div style="padding: 1.5rem; background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%); border-radius: 16px; border: 1px solid #bae6fd; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
                        <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem;">
                            <i class="fas fa-palette" style="color: #0284c7; font-size: 1.25rem;"></i>
                            <span style="font-size: 0.9rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; color: #0369a1;">Alternative Colors</span>
                        </div>
                        <div style="display: flex; flex-wrap: wrap; gap: 0.5rem;">
                            ${product.otherColors.map(c => `<span style="padding: 0.4rem 0.8rem; background: white; border-radius: 8px; color: #0c4a6e; font-weight: 600; font-size: 0.95rem; border: 1px solid #bae6fd;">${escapeHtml(c)}</span>`).join('')}
                        </div>
                    </div>
                ` : ''}
                
                ${Array.isArray(product.otherPrices) && product.otherPrices.length > 0 ? `
                    <div style="padding: 1.5rem; background: linear-gradient(135deg, #fdf4ff 0%, #fae8ff 100%); border-radius: 16px; border: 1px solid #f5d0fe; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
                        <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem;">
                            <i class="fas fa-tags" style="color: #a21caf; font-size: 1.25rem;"></i>
                            <span style="font-size: 0.9rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; color: #86198f;">Pricing & Discounts</span>
                        </div>
                        <ul style="margin: 0; padding: 0; list-style: none; display: flex; flex-direction: column; gap: 0.5rem;">
                            ${product.otherPrices.map(p => `
                                <li style="padding: 0.75rem; background: white; border-radius: 10px; color: #701a75; font-size: 0.95rem; font-weight: 600; border: 1px solid #f5d0fe; display: flex; align-items: center; gap: 0.5rem;">
                                    <i class="fas fa-percent" style="font-size: 0.75rem; opacity: 0.6;"></i>
                                    ${escapeHtml(p)}
                                </li>
                            `).join('')}
                        </ul>
                    </div>
                ` : ''}
            </div>
        `;
    }

    overviewHtml += extraDetailsHtml;

    const content = `
        <div style="display: grid; gap: 2rem;">
            <!-- Product Header -->
            <div style="display: grid; grid-template-columns: 250px 1fr; gap: 2rem; align-items: start; background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%); padding: 2rem; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); border: 1px solid rgba(0,0,0,0.06);">
                <div style="border-radius: 16px; overflow: hidden; box-shadow: 0 8px 24px rgba(0,0,0,0.15);">
                    <img src="${escapeHtml(image)}" 
                         alt="${escapeHtml(headerName)}" 
                         style="width: 100%; height: 250px; object-fit: cover; display: block;">
                </div>
                <div style="display: flex; flex-direction: column; gap: 1rem;">
                    <div>
                        <h2 style="margin: 0 0 0.75rem 0; font-size: 1.75rem; font-weight: 700; color: var(--gray-900); line-height: 1.3;">${escapeHtml(headerName)}</h2>
                        ${headerCategory ? `<span style="display: inline-block; padding: 0.5rem 1rem; background: var(--primary-light); color: var(--primary); border-radius: 24px; font-size: 0.85rem; font-weight: 700; letter-spacing: 0.5px;">${escapeHtml(headerCategory)}</span>` : ''}
                    </div>
                    <div style="font-size: 2.5rem; font-weight: 800; color: var(--primary); margin: 0.5rem 0;">
                        $${price ? parseFloat(price).toFixed(2) : 'N/A'}
                    </div>
                    <p style="color: var(--gray-600); line-height: 1.8; margin: 0; font-size: 1rem;">${escapeHtml(product.manualOverview || 'View the detailed overview below for comprehensive product information')}</p>
                </div>
            </div>

            ${overviewHtml}
        </div>
    `;

    document.getElementById('productContent').innerHTML = content;
}

function safeJsonParse(str) {
    try {
        return JSON.parse(str);
    } catch (e) {
        return null;
    }
}

function renderAiOverview(data) {
    const sections = [
        ['Basic Product Information', data.basicInfo],
        ['Identifiers & Codes', data.identifiers],
        ['Description & Content', data.descriptionContent],
        ['Pricing & Offers', data.pricingOffers],
        ['Inventory & Fulfillment', data.inventoryFulfillment],
        ['Media & Marketing', data.mediaMarketing],
        ['Compliance & Policies', data.compliancePolicies],
        ['Product Understanding', data.productUnderstanding],
        ['Usage & Compatibility', data.usageCompatibility],
        ['Order, Delivery & Packaging', data.deliveryPackaging],
        ['Returns, Refunds & Warranty', data.returnsRefundsWarranty]
    ];

    const blocks = sections
        .filter(([, obj]) => obj && Object.keys(obj).length > 0)
        .map(([title, obj]) => `
            <div style="background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%); border-radius: 16px; padding: 1.75rem; box-shadow: 0 4px 12px rgba(0,0,0,0.08); border: 1px solid rgba(0,0,0,0.06);">
                <h3 style="margin: 0 0 1.25rem 0; font-size: 1.25rem; color: var(--gray-900); display: flex; align-items: center; gap: 0.75rem; padding-bottom: 1rem; border-bottom: 2px solid var(--primary-light);">
                    <i class="fas fa-list" style="color: var(--primary); font-size: 1.1rem;"></i>
                    ${escapeHtml(title)}
                </h3>
                ${renderKeyValueGrid(obj)}
            </div>
        `).join('');

    return `
        <div style="display: grid; gap: 2rem; margin-top: 1.5rem;">
            <div style="background: linear-gradient(135deg, rgba(99,102,241,0.1) 0%, rgba(99,102,241,0.05) 100%); border: 2px solid rgba(99,102,241,0.2); border-radius: 12px; padding: 1.25rem; color: var(--gray-700); display: flex; align-items: center; gap: 0.75rem;">
                <i class="fas fa-robot" style="color: var(--primary); font-size: 1.5rem;"></i>
                <div>
                    <strong style="font-size: 1.05rem;">AI-Generated Overview</strong>
                    <div style="font-size: 0.9rem; color: var(--gray-600); margin-top: 0.25rem;">Comprehensive product details generated for support assistance</div>
                </div>
            </div>
            ${blocks || `<div class="text-muted">AI overview JSON was empty.</div>`}
        </div>
    `;
}

function renderKeyValueGrid(obj) {
    const entries = Object.entries(obj || {});
    if (!entries.length) return '<div class="text-muted">No data.</div>';

    return `
        <table style="width: 100%; border-collapse: collapse; margin-top: 1rem;">
            ${entries.map(([k, v], index) => `
                <tr style="background: ${index % 2 === 0 ? 'white' : '#f8f9fa'};">
                    <td style="padding: 1rem 1.25rem; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px; color: var(--gray-600); font-weight: 700; width: 35%; border-bottom: 1px solid #e9ecef; vertical-align: top;">
                        ${escapeHtml(String(k).replace(/([A-Z])/g, ' $1').trim())}
                    </td>
                    <td style="padding: 1rem 1.25rem; color: var(--gray-900); font-weight: 500; font-size: 1rem; line-height: 1.6; border-bottom: 1px solid #e9ecef; white-space: pre-wrap;">
                        ${escapeHtml(formatAny(v))}
                    </td>
                </tr>
            `).join('')}
        </table>
    `;
}

function formatAny(v) {
    if (v === null || v === undefined) return '';
    if (Array.isArray(v)) return v.map(x => formatAny(x)).join('\n- ');
    if (typeof v === 'object') return JSON.stringify(v, null, 2);
    return String(v);
}

function escapeHtml(str) {
    return String(str ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function closeProductModal() {
    const modal = document.getElementById('productModal');
    if (modal) {
        modal.classList.remove('active');
    }
    currentProductId = null;
}

// Close modal when clicking outside
document.addEventListener('click', (e) => {
    const productModal = document.getElementById('productModal');
    const aiSuggestModal = document.getElementById('aiSuggestModal');
    const draftsModal = document.getElementById('draftsModal');

    if (e.target === productModal) closeProductModal();
    if (e.target === aiSuggestModal) closeAiSuggestions();
    if (e.target === draftsModal) closeDraftsModal();
});

// Enter key to send message
document.addEventListener('keypress', (e) => {
    if (e.key === 'Enter' && e.target.id === 'messageInput') {
        sendMessage();
    }
});
