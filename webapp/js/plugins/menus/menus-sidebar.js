 // JavaScript pour gérer les sous-menus avec protection contre les conflits d'ID
document.addEventListener('DOMContentLoaded', function() {
    
    // Fonction pour initialiser les sous-menus
    function initializeSubmenus() {
        // Sélectionner uniquement les liens de sous-menu dans le contexte de la sidebar/offcanvas
        const sidebarContainer = document.getElementById('sidebarOffcanvas');
        
        if (!sidebarContainer) {
            console.warn('Container sidebarOffcanvas non trouvé');
            return;
        }
        
        // Sélectionner tous les liens avec data-bs-toggle="collapse" dans le container
        const submenuLinks = sidebarContainer.querySelectorAll('a[data-bs-toggle="collapse"]');
        
        submenuLinks.forEach(function(link) {
            // Récupérer l'ID cible du href
            const targetHref = link.getAttribute('href');
            
            if (targetHref && targetHref.startsWith('#')) {
                const targetId = targetHref.substring(1);
                
                // S'assurer que la cible existe dans le même container
                const targetElement = sidebarContainer.querySelector('#' + targetId);
                
                if (targetElement) {
                    // Initialiser Bootstrap Collapse pour cet élément
                    let collapseInstance = bootstrap.Collapse.getInstance(targetElement);
                    if (!collapseInstance) {
                        collapseInstance = new bootstrap.Collapse(targetElement, {
                            toggle: false
                        });
                    }
                    
                    // Ajouter l'événement click avec protection
                    link.addEventListener('click', function(event) {
                        event.preventDefault();
                        event.stopPropagation();
                        
                        // Vérifier à nouveau que l'élément cible est bien dans notre container
                        const currentTarget = sidebarContainer.querySelector(targetHref);
                        
                        if (currentTarget && currentTarget === targetElement) {
                            // Obtenir l'instance Bootstrap Collapse
                            const collapse = bootstrap.Collapse.getInstance(currentTarget);
                            
                            if (collapse) {
                                // Toggle du collapse
                                collapse.toggle();
                                
                                // Gérer l'icône de la flèche
                                updateArrowIcon(link, currentTarget);
                            }
                        }
                    });
                    
                    // Écouter les événements Bootstrap pour mettre à jour les icônes
                    targetElement.addEventListener('show.bs.collapse', function() {
                        updateArrowIcon(link, targetElement, true);
                    });
                    
                    targetElement.addEventListener('hide.bs.collapse', function() {
                        updateArrowIcon(link, targetElement, false);
                    });
                }
            }
        });
    }
    
    // Fonction pour mettre à jour l'icône de la flèche
    function updateArrowIcon(linkElement, targetElement, isExpanding = null) {
        const arrowIcon = linkElement.querySelector('.dropdown-arrow');
        
        if (arrowIcon) {
            // Si isExpanding n'est pas fourni, détecter l'état
            if (isExpanding === null) {
                isExpanding = targetElement.classList.contains('show') || 
                             targetElement.classList.contains('showing');
            }
            
            if (isExpanding) {
                // Rotation vers le haut quand ouvert
                arrowIcon.style.transform = 'rotate(180deg)';
                linkElement.setAttribute('aria-expanded', 'true');
            } else {
                // Rotation normale quand fermé
                arrowIcon.style.transform = 'rotate(0deg)';
                linkElement.setAttribute('aria-expanded', 'false');
            }
        }
    }
    
    // Fonction pour fermer tous les sous-menus (utile pour mobile)
    function closeAllSubmenus() {
        const sidebarContainer = document.getElementById('sidebarOffcanvas');
        
        if (sidebarContainer) {
            const openSubmenus = sidebarContainer.querySelectorAll('.collapse.show');
            
            openSubmenus.forEach(function(submenu) {
                const collapse = bootstrap.Collapse.getInstance(submenu);
                if (collapse) {
                    collapse.hide();
                }
            });
        }
    }
    
    // Fonction pour gérer la fermeture automatique sur mobile
    function handleMobileClosing() {
        const offcanvasElement = document.getElementById('sidebarOffcanvas');
        
        if (offcanvasElement) {
            // Écouter la fermeture de l'offcanvas
            offcanvasElement.addEventListener('hide.bs.offcanvas', function() {
                // Fermer tous les sous-menus quand l'offcanvas se ferme
                closeAllSubmenus();
            });
        }
    }
    
    // Fonction pour gérer les clics sur les liens normaux (non-dropdown)
    function handleRegularLinks() {
        const sidebarContainer = document.getElementById('sidebarOffcanvas');
        
        if (sidebarContainer) {
            const regularLinks = sidebarContainer.querySelectorAll('a.nav-link:not([data-bs-toggle="collapse"])');
            
            regularLinks.forEach(function(link) {
                link.addEventListener('click', function() {
                    // Retirer la classe active de tous les liens
                    const allLinks = sidebarContainer.querySelectorAll('a.nav-link');
                    allLinks.forEach(l => l.classList.remove('active'));
                    
                    // Ajouter la classe active au lien cliqué
                    this.classList.add('active');
                    
                    // Sur mobile, fermer l'offcanvas après le clic
                    if (window.innerWidth < 1200) { // xl breakpoint
                        const offcanvas = bootstrap.Offcanvas.getInstance(sidebarContainer);
                        if (offcanvas) {
                            offcanvas.hide();
                        }
                    }
                });
            });
        }
    }
    
    // Initialisation
    initializeSubmenus();
    handleMobileClosing();
    handleRegularLinks();
    
    // Réinitialiser si le DOM change (utile pour du contenu dynamique)
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
                // Vérifier si de nouveaux éléments de menu ont été ajoutés
                const hasNewMenuItems = Array.from(mutation.addedNodes).some(node => 
                    node.nodeType === Node.ELEMENT_NODE && 
                    (node.querySelector && node.querySelector('a[data-bs-toggle="collapse"]'))
                );
                
                if (hasNewMenuItems) {
                    initializeSubmenus();
                    handleRegularLinks();
                }
            }
        });
    });
    
    // Observer les changements dans le container de la sidebar
    const sidebarContainer = document.getElementById('sidebarOffcanvas');
    if (sidebarContainer) {
        observer.observe(sidebarContainer, {
            childList: true,
            subtree: true
        });
    }
    
    // Exposer des fonctions utilitaires globalement si nécessaire
    window.SidebarMenuUtils = {
        closeAllSubmenus: closeAllSubmenus,
        reinitialize: function() {
            initializeSubmenus();
            handleRegularLinks();
        }
    };
});