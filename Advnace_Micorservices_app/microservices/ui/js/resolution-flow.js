// Resolution Flow Implementation
// This file contains the complete implementation for the ticket resolution flow with customer confirmation and rating

// Global variables for resolution flow
let currentTicketStatus = null;
let hasExecutiveReplied = false;

// ==================== EXECUTIVE SIDE ====================

/**
 * Send a resolution request to the customer
 * This creates a special message with messageType: RESOLUTION_REQUEST
 */
async function sendResolutionRequest() {
    if (!currentTicketId) {
        showToast('No ticket selected', 'error');
        return;
    }

    const resolutionMessage = "Are all your questions answered? Would you like me to mark this ticket as resolved?";

    try {
        await apiCall(`/executive/tickets/${currentTicketId}/messages`, {
            method: 'POST',
            body: JSON.stringify({
                ticketId: currentTicketId,
                senderId: currentUser.id,
                senderType: 'EXECUTIVE',
                content: resolutionMessage,
                messageType: 'RESOLUTION_REQUEST'
            })
        });

        showToast('Resolution request sent', 'success');

        // Reload messages
        const messages = await apiCall(`/executive/tickets/${currentTicketId}/messages`);
        renderMessages(messages);

        // Dedicated Live Polling (keeps active to see ratings/feedback in real-time)
        if (pollingTimer) {
            clearInterval(pollingTimer);
        }
        pollingTimer = setInterval(() => loadMessages(currentTicketId), 3000);

        // Hide resolve button temporarily
        const btnResolve = document.getElementById('btnResolve');
        if (btnResolve) {
            btnResolve.style.display = 'none';
        }

    } catch (error) {
        showToast('Failed to send resolution request', 'error');
    }
}

/**
 * Check if customer has rated the ticket and display it
 */
async function checkAndDisplayRating() {
    if (!currentTicketId) return;

    try {
        const ticket = await apiCall(`/executive/tickets/${currentTicketId}`);
        const isResolved = ticket.status === 'RESOLVED' || ticket.status === 'CLOSED';

        if (isResolved) {
            const actionsCard = document.querySelector('.card.mb-4');
            const ratingHtml = ticket.rating && ticket.rating > 0
                ? `
                   <div style="background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%); padding: 1.5rem; border-radius: 12px; text-align: center;">
                        <h4 style="color: #92400e; margin: 0 0 0.5rem 0; font-size: 1rem;">Customer Satisfaction</h4>
                        <div style="display: flex; align-items: center; justify-content: center; gap: 0.5rem; margin: 1rem 0;">
                            ${generateStarDisplay(ticket.rating)}
                        </div>
                        <span style="font-size: 1.5rem; font-weight: 800; color: #92400e;">${ticket.rating} / 5</span>
                    </div>
                `
                : `
                    <div style="text-align: center; padding: 1rem; color: var(--text-muted);">
                        <i class="fas fa-hourglass-half" style="font-size: 1.5rem; margin-bottom: 0.5rem; opacity: 0.5;"></i>
                        <p style="margin: 0; font-size: 0.9rem;">Waiting for customer rating...</p>
                    </div>
                `;

            if (actionsCard) {
                const header = actionsCard.querySelector('.card-header .card-title');
                if (header) header.textContent = 'Feedback';

                const body = actionsCard.querySelector('.card-body');
                if (body) {
                    body.innerHTML = ratingHtml;
                    actionsCard.style.border = ticket.rating > 0 ? '2px solid #fbbf24' : '1px solid var(--border-color)';
                }
            }
        }
    } catch (error) {
        console.error('Error checking rating:', error);
    }
}

/**
 * Generate star display for rating
 */
function generateStarDisplay(rating) {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= rating) {
            stars += '<i class="fas fa-star" style="color: #fbbf24; font-size: 1.25rem;"></i>';
        } else {
            stars += '<i class="far fa-star" style="color: #d1d5db; font-size: 1.25rem;"></i>';
        }
    }
    return stars;
}

// ==================== CUSTOMER SIDE ====================

/**
 * Enhanced message rendering for customer side
 * Detects RESOLUTION_REQUEST messages and renders Yes/No buttons
 */
function renderMessagesWithResolution(messages, ticketStatus) {
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

    const isResolvedStatus = ticketStatus === 'RESOLVED' || ticketStatus === 'CLOSED';

    container.innerHTML = messages.map((msg, index) => {
        const isMe = msg.senderId === currentUser.id;
        const displayName = isMe ? 'You' : (msg.senderName || 'Support Executive');

        // Check if this is a resolution request
        const isResolutionRequest = msg.messageType === 'RESOLUTION_REQUEST' ||
            (msg.content && msg.content.includes("Are all your questions answered?"));

        if (isResolutionRequest && !isMe) {
            // Check if there's a response message AFTER this specific request
            const hasSubsequentResponse = messages.slice(index + 1).some(m => m.messageType === 'RESOLUTION_RESPONSE');
            const showButtons = !hasSubsequentResponse && !isResolvedStatus;

            return `
                <div class="message-wrapper received resolution-request">
                    <div class="message-sender">
                        <i class="fas fa-headset"></i>
                        ${displayName}
                    </div>
                    <div class="message-bubble" style="background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%); border: 2px solid #3b82f6; color: #1e3a8a;">
                        ${msg.content}
                        ${showButtons ? `
                        <div style="margin-top: 1rem; display: flex; gap: 0.5rem; justify-content: flex-end;">
                            <button class="btn btn-success btn-sm" onclick="confirmResolution(true)" style="padding: 0.4rem 0.8rem; font-size: 0.8rem; border-radius: 8px;">
                                <i class="fas fa-check"></i> Yes, Close
                            </button>
                            <button class="btn btn-danger btn-sm" onclick="confirmResolution(false)" style="padding: 0.4rem 0.8rem; font-size: 0.8rem; border-radius: 8px;">
                                <i class="fas fa-times"></i> Not Yet
                            </button>
                        </div>
                        ` : ''}
                        <span class="message-time">${formatDate(msg.createdAt)}</span>
                    </div>
                </div>
            `;
        }

        // Regular message rendering
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

/**
 * Handle customer's response to resolution request
 * @param {boolean} isResolved - true if customer confirms resolution, false otherwise
 */
async function confirmResolution(isResolved) {
    if (!currentTicketId) return;

    // Immediately hide the request buttons to prevent multiple clicks
    const requestActions = document.querySelector('.resolution-request .message-bubble div[style*="display: flex; gap: 0.75rem"]');
    if (requestActions) {
        requestActions.style.display = 'none';
    }

    try {
        if (isResolved) {
            window.currentTicketStatus = 'RESOLVED';

            // Customer confirmed - auto-resolve ticket
            await apiCall(`/tickets/${currentTicketId}/status`, {
                method: 'POST',
                body: JSON.stringify({ status: 'RESOLVED' })
            });

            // Send confirmation message
            await apiCall(`/customer/messages`, {
                method: 'POST',
                body: JSON.stringify({
                    ticketId: currentTicketId,
                    senderId: currentUser.id,
                    senderType: 'CUSTOMER',
                    content: 'Yes, my issue has been resolved. Thank you!',
                    messageType: 'RESOLUTION_RESPONSE'
                })
            });

            showToast('Ticket resolved successfully!', 'success');

            // Update status badge if it exists
            const statusBadge = document.getElementById('ticketDetailStatus');
            if (statusBadge) {
                statusBadge.innerHTML = '<span class="badge badge-resolved">Resolved</span>';
            }

            // Disable chat input
            disableChatInput();

            // CRITICAL: Stop polling to prevent rating UI from being wiped out
            if (pollingTimer) {
                clearInterval(pollingTimer);
                pollingTimer = null;
            }

            // Show rating UI immediately without reloading messages
            // (reloading would wipe out the rating UI we're about to show)
            showRatingUI();

        } else {
            // Customer needs more help
            await apiCall(`/customer/messages`, {
                method: 'POST',
                body: JSON.stringify({
                    ticketId: currentTicketId,
                    senderId: currentUser.id,
                    senderType: 'CUSTOMER',
                    content: 'No, I still need assistance with this issue.',
                    messageType: 'RESOLUTION_RESPONSE'
                })
            });

            showToast('Message sent. Our team will continue to assist you.', 'info');

            // Reload messages
            loadMessages(currentTicketId);
        }
    } catch (error) {
        showToast('Failed to process your response', 'error');
    }
}

/**
 * Disable chat input after ticket is resolved
 */
function disableChatInput() {
    const messageInput = document.getElementById('messageInput');
    const chatInput = document.querySelector('.chat-input');

    if (messageInput) {
        messageInput.disabled = true;
        messageInput.placeholder = 'This ticket has been resolved';
    }

    if (chatInput) {
        chatInput.style.opacity = '0.5';
        chatInput.style.pointerEvents = 'none';
    }
}

/**
 * Show rating UI to customer after resolution
 */
function showRatingUI() {
    const chatMessages = document.getElementById('chatMessages');

    // Prevent duplicate rating UI
    if (document.querySelector('.rating-container')) {
        console.log('Rating UI already exists, skipping...');
        return;
    }

    const ratingHtml = `
        <div class="rating-container" style="background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%); padding: 2rem; border-radius: 16px; margin-top: 1rem; text-align: center; border: 2px solid #22c55e;">
            <h3 style="margin: 0 0 1rem 0; color: #166534; font-size: 1.5rem;">
                <i class="fas fa-check-circle" style="color: #22c55e;"></i> Ticket Resolved!
            </h3>
            <p style="color: #166534; margin-bottom: 1.5rem; font-size: 1.05rem;">How would you rate your support experience?</p>
            
            <div class="rating" style="display: flex; flex-direction: row-reverse; gap: 0.3rem; justify-content: center; margin-bottom: 1.5rem;">
                <input type="radio" id="star-5" name="star-radio" value="5">
                <label for="star-5">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path pathLength="360" d="M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z"></path></svg>
                </label>
                <input type="radio" id="star-4" name="star-radio" value="4">
                <label for="star-4">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path pathLength="360" d="M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z"></path></svg>
                </label>
                <input type="radio" id="star-3" name="star-radio" value="3">
                <label for="star-3">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path pathLength="360" d="M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z"></path></svg>
                </label>
                <input type="radio" id="star-2" name="star-radio" value="2">
                <label for="star-2">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path pathLength="360" d="M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z"></path></svg>
                </label>
                <input type="radio" id="star-1" name="star-radio" value="1">
                <label for="star-1">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path pathLength="360" d="M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z"></path></svg>
                </label>
            </div>
            
            <button class="btn btn-primary" onclick="submitRating()" id="submitRatingBtn" disabled>
                <i class="fas fa-star"></i> Submit Rating
            </button>
            
            <div id="ratingThankYou" style="display: none; margin-top: 1.5rem; color: #166534; font-weight: 600;">
                <i class="fas fa-heart" style="color: #ef4444;"></i> Thank you for your feedback!
            </div>
        </div>
    `;

    chatMessages.innerHTML += ratingHtml;
    chatMessages.scrollTop = chatMessages.scrollHeight;

    // Add event listeners to rating inputs
    document.querySelectorAll('input[name="star-radio"]').forEach(input => {
        input.addEventListener('change', () => {
            document.getElementById('submitRatingBtn').disabled = false;
        });
    });
}

/**
 * Submit customer rating
 */
async function submitRating() {
    const selectedRating = document.querySelector('input[name="star-radio"]:checked');

    if (!selectedRating) {
        showToast('Please select a rating', 'warning');
        return;
    }

    const rating = parseInt(selectedRating.value);

    try {
        await apiCall(`/tickets/${currentTicketId}/rating`, {
            method: 'POST',
            body: JSON.stringify({ rating })
        });

        showToast('Thank you for your rating!', 'success');

        // Hide rating form and show thank you
        document.querySelector('.rating').style.display = 'none';
        document.getElementById('submitRatingBtn').style.display = 'none';
        document.getElementById('ratingThankYou').style.display = 'block';

    } catch (error) {
        showToast('Failed to submit rating', 'error');
    }
}

// Export functions for use in main files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        sendResolutionRequest,
        confirmResolution,
        submitRating,
        renderMessagesWithResolution,
        checkAndDisplayRating
    };
}
