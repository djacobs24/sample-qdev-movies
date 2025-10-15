// Modal functionality for review form
function openReviewModal() {
    const modal = document.getElementById('reviewModal');
    if (modal) {
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden'; // Prevent background scrolling
    }
}

function closeReviewModal() {
    const modal = document.getElementById('reviewModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto'; // Restore scrolling
        
        // Clear any error messages
        const errorDiv = document.querySelector('.error-message');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
        
        // Reset form
        const form = document.getElementById('reviewForm');
        if (form) {
            form.reset();
            resetStarRating();
        }
    }
}

// Check if page was loaded after successful review submission
document.addEventListener('DOMContentLoaded', function() {
    // Check if URL has success parameter (you'll need to add this to controller)
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('reviewAdded') === 'true') {
        // Scroll to reviews section
        setTimeout(() => {
            const reviewsSection = document.querySelector('.reviews-section');
            if (reviewsSection) {
                reviewsSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        }, 100);
    }
});

// Star rating functionality
document.addEventListener('DOMContentLoaded', function() {
    const stars = document.querySelectorAll('.star');
    const ratingInput = document.getElementById('rating');
    
    stars.forEach((star, index) => {
        star.addEventListener('click', function() {
            const rating = index + 1;
            ratingInput.value = rating;
            
            // Update star display
            stars.forEach((s, i) => {
                if (i < rating) {
                    s.textContent = '★';
                    s.classList.add('selected');
                } else {
                    s.textContent = '☆';
                    s.classList.remove('selected');
                }
            });
        });
        
        star.addEventListener('mouseover', function() {
            const rating = index + 1;
            
            // Highlight stars on hover
            stars.forEach((s, i) => {
                if (i < rating) {
                    s.textContent = '★';
                } else {
                    s.textContent = '☆';
                }
            });
        });
    });
    
    // Reset stars when mouse leaves the rating area
    const starRating = document.querySelector('.star-rating');
    if (starRating) {
        starRating.addEventListener('mouseleave', function() {
            const currentRating = parseInt(ratingInput.value) || 0;
            
            stars.forEach((s, i) => {
                if (i < currentRating) {
                    s.textContent = '★';
                    s.classList.add('selected');
                } else {
                    s.textContent = '☆';
                    s.classList.remove('selected');
                }
            });
        });
    }
});

function resetStarRating() {
    const stars = document.querySelectorAll('.star');
    const ratingInput = document.getElementById('rating');
    
    ratingInput.value = '';
    stars.forEach(star => {
        star.textContent = '☆';
        star.classList.remove('selected');
    });
}

function validateForm() {
    const ratingInput = document.getElementById('rating');
    
    if (!ratingInput.value || ratingInput.value === '') {
        alert('Please select a rating before submitting your review.');
        return false;
    }
    
    return true;
}

// Close modal when clicking outside of it
window.addEventListener('click', function(event) {
    const modal = document.getElementById('reviewModal');
    if (event.target === modal) {
        closeReviewModal();
    }
});

// Close modal with Escape key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeReviewModal();
    }
});
