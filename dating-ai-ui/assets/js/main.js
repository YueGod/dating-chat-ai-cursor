/**
 * Dating AI - Main JavaScript File
 */

document.addEventListener('DOMContentLoaded', () => {
    // Initialize Feather icons if available
    if (typeof feather !== 'undefined') {
        feather.replace();
    }

    // Initialize all components
    initTooltips();
    initDropdowns();
    initTabs();
    initCategoryPills();
    initModals();
    initFormValidation();
    initCharacterSearch();
    initChatInput();
    initAnimations();

    // Mobile menu toggle
    const mobileMenuToggle = document.getElementById('mobile-menu-toggle');
    const mobileMenu = document.getElementById('mobile-menu');

    if (mobileMenuToggle && mobileMenu) {
        mobileMenuToggle.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
        });
    }

    // Back button functionality
    const backButton = document.getElementById('back-button');
    if (backButton) {
        backButton.addEventListener('click', () => {
            history.back();
        });
    }
});

/**
 * Toast notification system
 */
const toast = {
    container: null,

    init() {
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.className = 'toast-container';
            document.body.appendChild(this.container);
        }
    },

    show(message, type = 'info', duration = 3000, title = '') {
        this.init();

        const id = 'toast-' + Date.now();
        const toast = document.createElement('div');
        toast.className = `toast toast--${type}`;
        toast.id = id;

        let iconName = 'info';
        if (type === 'success') iconName = 'check-circle';
        if (type === 'error') iconName = 'alert-circle';
        if (type === 'warning') iconName = 'alert-triangle';

        toast.innerHTML = `
            <div class="toast__icon toast__icon--${type}">
                <i data-feather="${iconName}"></i>
            </div>
            <div class="toast__content">
                ${title ? `<div class="toast__title">${title}</div>` : ''}
                <div class="toast__message">${message}</div>
            </div>
            <button class="toast__close" onclick="toast.hide('${id}')">
                <i data-feather="x" width="16" height="16"></i>
            </button>
        `;

        this.container.appendChild(toast);

        if (typeof feather !== 'undefined') {
            feather.replace();
        }

        setTimeout(() => this.hide(id), duration);

        return id;
    },

    hide(id) {
        const toast = document.getElementById(id);
        if (toast) {
            toast.classList.add('toast--exiting');
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        }
    },

    success(message, duration, title) {
        return this.show(message, 'success', duration, title);
    },

    error(message, duration, title) {
        return this.show(message, 'error', duration, title);
    },

    warning(message, duration, title) {
        return this.show(message, 'warning', duration, title);
    },

    info(message, duration, title) {
        return this.show(message, 'info', duration, title);
    }
};

/**
 * Form validation
 */
function initFormValidation() {
    const forms = document.querySelectorAll('form[data-validate]');

    forms.forEach(form => {
        form.addEventListener('submit', (e) => {
            let isValid = true;

            // Find all required inputs
            const requiredInputs = form.querySelectorAll('[required]');

            requiredInputs.forEach(input => {
                if (!validateInput(input)) {
                    isValid = false;
                }
            });

            if (!isValid) {
                e.preventDefault();
                toast.error('请填写所有必填字段', 3000, '表单验证失败');
            }
        });

        // Validate inputs on blur
        const inputs = form.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            input.addEventListener('blur', () => {
                if (input.hasAttribute('required') || input.value !== '') {
                    validateInput(input);
                }
            });

            // Remove error styles on input
            input.addEventListener('input', () => {
                input.classList.remove('input-error');
                const errorElement = input.parentNode.querySelector('.error-message');
                if (errorElement) {
                    errorElement.remove();
                }
            });
        });
    });
}

function validateInput(input) {
    // Remove previous error
    const parent = input.parentNode;
    const existingError = parent.querySelector('.error-message');
    if (existingError) {
        existingError.remove();
    }

    input.classList.remove('input-error');

    // Check if empty
    if (input.hasAttribute('required') && !input.value.trim()) {
        showInputError(input, '此字段不能为空');
        return false;
    }

    // Email validation
    if (input.type === 'email' && input.value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(input.value)) {
            showInputError(input, '请输入有效的电子邮箱地址');
            return false;
        }
    }

    // Password validation
    if (input.type === 'password' && input.hasAttribute('data-min-length')) {
        const minLength = parseInt(input.getAttribute('data-min-length'));
        if (input.value.length < minLength) {
            showInputError(input, `密码长度至少为 ${minLength} 个字符`);
            return false;
        }
    }

    // Password confirmation
    if (input.hasAttribute('data-confirm-password')) {
        const passwordInput = document.getElementById(input.getAttribute('data-confirm-password'));
        if (passwordInput && input.value !== passwordInput.value) {
            showInputError(input, '两次输入的密码不一致');
            return false;
        }
    }

    // Custom validation
    if (input.hasAttribute('data-validate-fn')) {
        const validateFn = window[input.getAttribute('data-validate-fn')];
        if (typeof validateFn === 'function') {
            const result = validateFn(input.value);
            if (result !== true) {
                showInputError(input, result);
                return false;
            }
        }
    }

    return true;
}

function showInputError(input, message) {
    input.classList.add('input-error');

    const errorElement = document.createElement('div');
    errorElement.className = 'error-message';
    errorElement.textContent = message;
    errorElement.style.color = 'var(--color-danger)';
    errorElement.style.fontSize = '0.75rem';
    errorElement.style.marginTop = '0.25rem';

    input.parentNode.appendChild(errorElement);

    input.focus();
}

/**
 * Modal functionality
 */
function initModals() {
    // Modal triggers
    const modalTriggers = document.querySelectorAll('[data-modal-target]');

    modalTriggers.forEach(trigger => {
        trigger.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = trigger.getAttribute('data-modal-target');
            const modal = document.getElementById(targetId);

            if (modal) {
                openModal(modal);
            }
        });
    });

    // Close buttons
    const closeButtons = document.querySelectorAll('.modal__close, [data-modal-close]');

    closeButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            const modal = button.closest('.modal');

            if (modal) {
                closeModal(modal);
            }
        });
    });

    // Close on background click
    const modals = document.querySelectorAll('.modal');

    modals.forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeModal(modal);
            }
        });
    });

    // Character info modal
    const infoButton = document.getElementById('info-button');
    const characterModal = document.getElementById('character-modal');

    if (infoButton && characterModal) {
        infoButton.addEventListener('click', () => {
            openModal(characterModal);
        });
    }
}

function openModal(modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';

    // Trap focus
    trapFocus(modal);
}

function closeModal(modal) {
    modal.classList.remove('active');
    document.body.style.overflow = '';
}

/**
 * Focus trap for modals
 */
function trapFocus(element) {
    const focusableElements = element.querySelectorAll('a[href], button, textarea, input[type="text"], input[type="checkbox"], input[type="radio"], select');
    const firstFocusable = focusableElements[0];
    const lastFocusable = focusableElements[focusableElements.length - 1];

    element.addEventListener('keydown', (e) => {
        if (e.key === 'Tab') {
            if (e.shiftKey && document.activeElement === firstFocusable) {
                e.preventDefault();
                lastFocusable.focus();
            } else if (!e.shiftKey && document.activeElement === lastFocusable) {
                e.preventDefault();
                firstFocusable.focus();
            }
        }

        if (e.key === 'Escape') {
            closeModal(element);
        }
    });

    firstFocusable.focus();
}

/**
 * Tabs functionality
 */
function initTabs() {
    const tabGroups = document.querySelectorAll('.tabs');

    tabGroups.forEach(tabGroup => {
        const tabs = tabGroup.querySelectorAll('.tab');
        const tabContents = document.querySelectorAll(`[data-tab-content="${tabGroup.getAttribute('data-tabs')}"]`);

        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const tabId = tab.getAttribute('data-tab');

                // Update active tab
                tabs.forEach(t => t.classList.remove('tab--active'));
                tab.classList.add('tab--active');

                // Show corresponding content
                tabContents.forEach(content => {
                    if (content.getAttribute('data-tab-id') === tabId) {
                        content.style.display = 'block';
                    } else {
                        content.style.display = 'none';
                    }
                });

                // Animated tab slider (if exists)
                const tabSlider = tabGroup.querySelector('.tabs-slider');
                if (tabSlider) {
                    tabSlider.style.width = `${tab.offsetWidth}px`;
                    tabSlider.style.left = `${tab.offsetLeft}px`;
                }
            });
        });

        // Initialize first tab as active if none are active
        if (!tabGroup.querySelector('.tab--active') && tabs.length > 0) {
            tabs[0].click();
        }
    });
}

/**
 * Category Pills
 */
function initCategoryPills() {
    const categoryGroups = document.querySelectorAll('.category-pills');

    categoryGroups.forEach(group => {
        const pills = group.querySelectorAll('.category-pill');

        pills.forEach(pill => {
            pill.addEventListener('click', () => {
                const isActive = pill.classList.contains('category-pill--active');

                // If it's a single selection group
                if (group.hasAttribute('data-single-select')) {
                    pills.forEach(p => p.classList.remove('category-pill--active'));

                    if (!isActive) {
                        pill.classList.add('category-pill--active');
                    }
                } else {
                    // Toggle active state for multiple selection
                    pill.classList.toggle('category-pill--active');
                }

                // Trigger category change event
                const event = new CustomEvent('categoryChange', {
                    detail: {
                        category: pill.getAttribute('data-category'),
                        active: pill.classList.contains('category-pill--active')
                    }
                });

                document.dispatchEvent(event);
            });
        });
    });
}

/**
 * Character Search
 */
function initCharacterSearch() {
    const searchInput = document.getElementById('character-search');
    const charactersGrid = document.getElementById('characters-grid');

    if (searchInput && charactersGrid) {
        const characterCards = charactersGrid.querySelectorAll('.character-card');
        const emptyState = document.getElementById('empty-state');

        searchInput.addEventListener('input', () => {
            const searchTerm = searchInput.value.toLowerCase().trim();
            let visibleCount = 0;

            characterCards.forEach(card => {
                // Get card data to search
                const name = card.querySelector('.character-card__name').textContent.toLowerCase();
                const description = card.querySelector('.character-card__description').textContent.toLowerCase();
                const subtitle = card.querySelector('.character-card__subtitle').textContent.toLowerCase();

                // Check for match
                if (name.includes(searchTerm) || description.includes(searchTerm) || subtitle.includes(searchTerm)) {
                    card.style.display = 'flex';
                    visibleCount++;
                } else {
                    card.style.display = 'none';
                }
            });

            // Toggle empty state
            if (emptyState) {
                if (visibleCount === 0) {
                    emptyState.style.display = 'block';
                    charactersGrid.style.display = 'none';
                } else {
                    emptyState.style.display = 'none';
                    charactersGrid.style.display = 'grid';
                }
            }
        });

        // Reset search button
        const resetSearchBtn = document.getElementById('reset-search');

        if (resetSearchBtn) {
            resetSearchBtn.addEventListener('click', () => {
                searchInput.value = '';

                // Show all cards
                characterCards.forEach(card => {
                    card.style.display = 'flex';
                });

                // Reset category pills
                document.querySelectorAll('.category-pill').forEach(pill => {
                    pill.classList.remove('category-pill--active');
                    if (pill.getAttribute('data-category') === 'all') {
                        pill.classList.add('category-pill--active');
                    }
                });

                // Hide empty state
                if (emptyState) {
                    emptyState.style.display = 'none';
                    charactersGrid.style.display = 'grid';
                }

                // Trigger custom event
                document.dispatchEvent(new CustomEvent('searchReset'));
            });
        }
    }
}

/**
 * Chat Input functionality
 */
function initChatInput() {
    const chatInput = document.querySelector('.chat-input__field');
    const sendButton = document.querySelector('.chat-input__button');
    const chatContainer = document.getElementById('chat-container');

    if (chatInput && sendButton && chatContainer) {
        // Auto resize input
        chatInput.addEventListener('input', () => {
            // Reset height first to get accurate scrollHeight
            chatInput.style.height = 'auto';

            // Set to scrollHeight but limit to 100px
            const newHeight = Math.min(chatInput.scrollHeight, 100);
            chatInput.style.height = newHeight + 'px';

            // Enable/disable send button based on input
            sendButton.disabled = chatInput.value.trim() === '';
        });

        // Send message on click or enter
        const sendMessage = () => {
            const message = chatInput.value.trim();

            if (message) {
                // Create user message
                appendUserMessage(message);

                // Clear and reset input
                chatInput.value = '';
                chatInput.style.height = 'auto';
                sendButton.disabled = true;

                // Scroll to bottom
                scrollChatToBottom();

                // Show typing indicator
                showTypingIndicator();

                // Simulate AI response after delay
                setTimeout(() => {
                    hideTypingIndicator();
                    simulateAIResponse();
                }, 1500);
            }
        };

        sendButton.addEventListener('click', sendMessage);

        chatInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });

        // Quick reply functionality
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('quick-reply')) {
                chatInput.value = e.target.textContent;
                sendMessage();
            }
        });
    }
}

function appendUserMessage(message) {
    const chatContainer = document.getElementById('chat-container');

    if (chatContainer) {
        const messageElement = document.createElement('div');
        messageElement.className = 'chat-message chat-message--user';

        messageElement.innerHTML = `
            <div class="chat-message__content">
                <div class="chat-bubble chat-bubble--user">${message}</div>
                <div class="chat-message__time">${formatTime(new Date())}</div>
            </div>
        `;

        chatContainer.appendChild(messageElement);
    }
}

function appendAIMessage(message) {
    const chatContainer = document.getElementById('chat-container');
    const characterAvatar = document.getElementById('character-avatar');
    const characterName = document.getElementById('character-name');

    if (chatContainer) {
        const messageElement = document.createElement('div');
        messageElement.className = 'chat-message chat-message--ai';

        messageElement.innerHTML = `
            <div class="chat-message__avatar">
                <img src="${characterAvatar ? characterAvatar.src : ''}" alt="${characterName ? characterName.textContent : 'AI'}">
            </div>
            <div class="chat-message__content">
                <div class="chat-bubble chat-bubble--ai">${message}</div>
                <div class="chat-message__time">${formatTime(new Date())}</div>
            </div>
        `;

        chatContainer.appendChild(messageElement);
    }
}

function showTypingIndicator() {
    const chatContainer = document.getElementById('chat-container');
    const characterAvatar = document.getElementById('character-avatar');
    const characterName = document.getElementById('character-name');

    if (chatContainer) {
        const existingIndicator = document.querySelector('.chat-message--typing');
        if (existingIndicator) return;

        const typingElement = document.createElement('div');
        typingElement.className = 'chat-message chat-message--ai chat-message--typing';

        typingElement.innerHTML = `
            <div class="chat-message__avatar">
                <img src="${characterAvatar ? characterAvatar.src : ''}" alt="${characterName ? characterName.textContent : 'AI'}">
            </div>
            <div class="chat-message__content">
                <div class="chat-bubble chat-bubble--ai chat-bubble--typing">
                    <div class="typing-indicator">
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>
                </div>
            </div>
        `;

        chatContainer.appendChild(typingElement);
        scrollChatToBottom();
    }
}

function hideTypingIndicator() {
    const typingIndicator = document.querySelector('.chat-message--typing');

    if (typingIndicator) {
        typingIndicator.remove();
    }
}

function scrollChatToBottom() {
    const chatContainer = document.getElementById('chat-container');

    if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }
}

function simulateAIResponse() {
    // In a real app, this would be replaced with an API call
    const responses = [
        "嗨，很高兴能和你聊天！我们可以聊聊你感兴趣的话题。你最近在做什么有趣的事情吗？",
        "我一直在想，如果有机会去一个地方旅行，你会去哪里呢？",
        "有时候我觉得读一本好书就像是和一个聪明的朋友聊天。你最近有读什么好书吗？",
        "我喜欢尝试不同的美食，无论是中餐、西餐还是其他国家的特色菜。你有什么最喜欢的美食吗？",
        "今天天气真好！这种天气最适合出去散步或者找个咖啡馆坐坐了。你喜欢什么样的天气？",
        "每个人都有自己独特的爱好，这让生活更加有趣。你平时有什么特别的爱好或兴趣吗？"
    ];

    const randomResponse = responses[Math.floor(Math.random() * responses.length)];
    appendAIMessage(randomResponse);

    // Add quick replies
    const quickReplies = [
        "我最近在学习新技能",
        "我喜欢去海边旅行",
        "我喜欢美食",
        "告诉我更多关于你的事情"
    ];

    appendQuickReplies(quickReplies);
    scrollChatToBottom();
}

function appendQuickReplies(replies) {
    const chatContainer = document.getElementById('chat-container');

    if (chatContainer) {
        // Remove existing quick replies
        const existingReplies = document.querySelector('.quick-replies');
        if (existingReplies) {
            existingReplies.remove();
        }

        const repliesElement = document.createElement('div');
        repliesElement.className = 'quick-replies';

        replies.forEach(reply => {
            const replyElement = document.createElement('div');
            replyElement.className = 'quick-reply';
            replyElement.textContent = reply;
            repliesElement.appendChild(replyElement);
        });

        chatContainer.appendChild(repliesElement);
    }
}

function formatTime(date) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

/**
 * Tooltip functionality
 */
function initTooltips() {
    const tooltipTriggers = document.querySelectorAll('[data-tooltip]');

    tooltipTriggers.forEach(trigger => {
        const tooltipText = trigger.getAttribute('data-tooltip');
        const tooltipPosition = trigger.getAttribute('data-tooltip-position') || 'top';

        trigger.addEventListener('mouseenter', () => {
            const tooltip = document.createElement('div');
            tooltip.className = `tooltip tooltip--${tooltipPosition}`;
            tooltip.textContent = tooltipText;

            document.body.appendChild(tooltip);

            const triggerRect = trigger.getBoundingClientRect();
            const tooltipRect = tooltip.getBoundingClientRect();

            let top, left;

            switch (tooltipPosition) {
                case 'top':
                    top = triggerRect.top - tooltipRect.height - 8;
                    left = triggerRect.left + (triggerRect.width / 2) - (tooltipRect.width / 2);
                    break;
                case 'bottom':
                    top = triggerRect.bottom + 8;
                    left = triggerRect.left + (triggerRect.width / 2) - (tooltipRect.width / 2);
                    break;
                case 'left':
                    top = triggerRect.top + (triggerRect.height / 2) - (tooltipRect.height / 2);
                    left = triggerRect.left - tooltipRect.width - 8;
                    break;
                case 'right':
                    top = triggerRect.top + (triggerRect.height / 2) - (tooltipRect.height / 2);
                    left = triggerRect.right + 8;
                    break;
            }

            tooltip.style.top = `${top + window.scrollY}px`;
            tooltip.style.left = `${left + window.scrollX}px`;
            tooltip.style.opacity = '1';

            trigger.addEventListener('mouseleave', () => {
                tooltip.remove();
            });
        });
    });
}

/**
 * Dropdown functionality
 */
function initDropdowns() {
    const dropdownTriggers = document.querySelectorAll('[data-dropdown]');

    dropdownTriggers.forEach(trigger => {
        const dropdownId = trigger.getAttribute('data-dropdown');
        const dropdown = document.getElementById(dropdownId);

        if (dropdown) {
            trigger.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();

                dropdown.classList.toggle('dropdown--active');

                // Position dropdown
                const triggerRect = trigger.getBoundingClientRect();
                dropdown.style.top = `${triggerRect.bottom + 5}px`;
                dropdown.style.left = `${triggerRect.left}px`;

                // Close other dropdowns
                document.querySelectorAll('.dropdown--active').forEach(activeDropdown => {
                    if (activeDropdown !== dropdown) {
                        activeDropdown.classList.remove('dropdown--active');
                    }
                });
            });
        }
    });

    // Close dropdown on outside click
    document.addEventListener('click', () => {
        document.querySelectorAll('.dropdown--active').forEach(dropdown => {
            dropdown.classList.remove('dropdown--active');
        });
    });
}

/**
 * Animations
 */
function initAnimations() {
    // Animate elements when they enter viewport
    const animatedElements = document.querySelectorAll('[data-animate]');

    if (animatedElements.length > 0) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const element = entry.target;
                    const animation = element.getAttribute('data-animate');
                    const delay = element.getAttribute('data-animate-delay') || 0;

                    setTimeout(() => {
                        element.classList.add(`animate-${animation}`);
                        element.style.opacity = '1';
                    }, delay);

                    observer.unobserve(element);
                }
            });
        }, {
            threshold: 0.1
        });

        animatedElements.forEach(element => {
            element.style.opacity = '0';
            observer.observe(element);
        });
    }

    // Particle animation for hero section
    const heroSection = document.querySelector('.hero');

    if (heroSection && heroSection.getAttribute('data-particles') === 'true') {
        createParticles(heroSection);
    }
}

function createParticles(container) {
    const canvas = document.createElement('canvas');
    canvas.style.position = 'absolute';
    canvas.style.top = '0';
    canvas.style.left = '0';
    canvas.style.width = '100%';
    canvas.style.height = '100%';
    canvas.style.pointerEvents = 'none';
    canvas.style.zIndex = '0';

    container.style.position = 'relative';
    container.prepend(canvas);

    const ctx = canvas.getContext('2d');
    let particles = [];

    function resizeCanvas() {
        canvas.width = container.offsetWidth;
        canvas.height = container.offsetHeight;
    }

    window.addEventListener('resize', resizeCanvas);
    resizeCanvas();

    // Create particles
    function createParticle() {
        return {
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height,
            size: Math.random() * 4 + 1,
            speedX: Math.random() * 1 - 0.5,
            speedY: Math.random() * 1 - 0.5,
            opacity: Math.random() * 0.5 + 0.1
        };
    }

    // Initialize particles
    for (let i = 0; i < 50; i++) {
        particles.push(createParticle());
    }

    // Animation loop
    function animate() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        particles.forEach(particle => {
            // Update position
            particle.x += particle.speedX;
            particle.y += particle.speedY;

            // Wrap around edges
            if (particle.x < 0) particle.x = canvas.width;
            if (particle.x > canvas.width) particle.x = 0;
            if (particle.y < 0) particle.y = canvas.height;
            if (particle.y > canvas.height) particle.y = 0;

            // Draw particle
            ctx.beginPath();
            ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2);
            ctx.fillStyle = `rgba(255, 255, 255, ${particle.opacity})`;
            ctx.fill();
        });

        requestAnimationFrame(animate);
    }

    animate();
}

/**
 * API calls - Replace with actual API endpoints
 */
const api = {
    baseUrl: '/api',

    async get(endpoint) {
        try {
            const response = await fetch(`${this.baseUrl}${endpoint}`);

            if (!response.ok) {
                throw new Error(`HTTP error ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('API GET error:', error);
            throw error;
        }
    },

    async post(endpoint, data) {
        try {
            const response = await fetch(`${this.baseUrl}${endpoint}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error(`HTTP error ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('API POST error:', error);
            throw error;
        }
    },

    // Character endpoints
    async getCharacters() {
        return this.get('/characters');
    },

    async getCharacterById(id) {
        return this.get(`/characters/${id}`);
    },

    // Chat endpoints
    async getChatHistory(characterId) {
        return this.get(`/chat/${characterId}/history`);
    },

    async sendMessage(characterId, message) {
        return this.post(`/chat/${characterId}/send`, { message });
    },

    // User endpoints
    async login(credentials) {
        return this.post('/user/login', credentials);
    },

    async register(userData) {
        return this.post('/user/register', userData);
    },

    async getUserProfile() {
        return this.get('/user/profile');
    },

    async updateUserProfile(profileData) {
        return this.post('/user/profile', profileData);
    }
}; 