/**
 * Gestion de l'affichage conditionnel des groupes d'éléments du formulaire d'ajout d'item
 * en fonction de la valeur sélectionnée dans le select item_type
 */

// Fonction utilitaire pour masquer un élément
function hideElement(element) {
    if (element) {
        element.setAttribute("hidden", "");
    }
}

// Fonction utilitaire pour afficher un élément
function showElement(element) {
    if (element) {
        element.removeAttribute("hidden");
    }
}

// Fonction utilitaire pour vider les champs d'un groupe
function clearGroup(group) {
    if (group) {
        const inputs = group.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            if (input.type === 'checkbox' || input.type === 'radio') {
                input.checked = false;
            } else {
                input.value = '';
            }
        });
    }
}

/**
 * Fonction pour gérer l'affichage des groupes d'éléments du formulaire
 */
function toggleFormGroups() {
	
    const itemTypeSelect = document.getElementById('item_type');
    const addItemForm = document.getElementById('add-item-form');
    
    if (!itemTypeSelect || !addItemForm) {
        console.warn('Éléments item_type ou add-item-form non trouvés');
        return;
    }
    
    // Récupération de tous les groupes
    const groupDynamique = document.getElementById('dynamic-group');
    const groupLabel = document.getElementById('label-group');
    const groupUrl = document.getElementById('url-group');
    const groupMenu = document.getElementById('menu-group');
	const itemUrlInput = document.getElementById('item_url');
	    
    // Vider tous les groupes sauf le type
    clearGroup(groupDynamique);
    clearGroup(groupLabel);
    clearGroup(groupUrl);
    clearGroup(groupMenu);
    
    // Masquer tous les groupes par défaut
    hideElement(groupDynamique);
    hideElement(groupLabel);
    hideElement(groupUrl);
    hideElement(groupMenu);
	  
    const selectedValue = itemTypeSelect.value;
    
    switch (selectedValue) {
        case 'page':
			showElement(groupLabel);
			showElement(groupUrl);
			if (itemUrlInput) itemUrlInput.value = 'page_id=';
			break;
        case 'xpage':
			showElement(groupLabel);
			showElement(groupUrl);
			if (itemUrlInput) itemUrlInput.value = 'page=';
        case 'external_url':
            showElement(groupLabel);
            showElement(groupUrl);
            break;
            
        case 'menu':
			showElement(groupLabel);
			showElement(groupUrl);
			showElement(groupMenu);
			if (itemUrlInput) itemUrlInput.value = '#';
			break;
            
        default:
            break;
    }
}

// Fonction utilitaire pour desactiver un élément
function disableElement(element) {
    if (element) {
        element.setAttribute("disabled", "true");
    }
}

// Fonction utilitaire pour activer un élément
function enableElement(element) {
    if (element) {
        element.removeAttribute("disabled");
    }
}

function updateDynamicCheckbox( ){
	
	const itemDynamicCheckbox = document.getElementById('item_dynamic');
	const itemLabelInput = document.getElementById('item_label');
	const groupLabel = document.getElementById('label-group');
	
	if(itemDynamicCheckbox.checked) 
	{	
		clearGroup(groupLabel);
		disableElement(itemLabelInput);
	} 
	else 
	{
		clearGroup(groupLabel);
		enableElement(itemLabelInput);
	}

}

/**
 * Fonction pour gérer le clic sur un élément de liste
 */
function handleListItemClick(event) {
    const listItem = event.currentTarget;
    const itemId = listItem.getAttribute('id');
    const itemType = listItem.getAttribute('data-type');
    const itemLabel = listItem.textContent.trim();
    
    // Mettre à jour le formulaire
    updateFormFromListItem(itemType, itemId, itemLabel);
}

/**
 * Fonction pour mettre à jour le formulaire selon l'élément sélectionné
 */
function updateFormFromListItem(type, id, label) 
{
	const itemIdInput = document.getElementById('source_id_item');
    const itemTypeSelect = document.getElementById('item_type');
    const itemLabelInput = document.getElementById('item_label');
    const itemUrlInput = document.getElementById('item_url');
    const itemTargetMenuSelect = document.getElementById('item_target_menu');
	const groupDynamic = document.getElementById('dynamic-group');
	
    if (!itemTypeSelect) return;
    
    // Définir le type dans le select
    itemTypeSelect.value = type;
    
    // Déclencher le changement pour mettre à jour l'affichage des groupes
    toggleFormGroups();
    
    // Remplir les champs selon le type
    switch (type) {
        case 'page':
			if (itemIdInput) itemIdInput.value=id;
            if (itemLabelInput && label) itemLabelInput.value = label.split('-')[0].trim( ).substring(0, 50);
            if (itemUrlInput) itemUrlInput.value = 'page_id='+id;
			showElement(groupDynamic);
            break;
            
        case 'xpage':
			if (itemIdInput) itemIdInput.value=id;    
			if (itemLabelInput && label) itemLabelInput.value = label.substring(0, 50);
            if (itemUrlInput) itemUrlInput.value = 'page='+id;
            break;
            
        case 'external_url':
			if (itemIdInput) itemIdInput.value=id;    
			if (itemLabelInput && label) itemLabelInput.value = label.substring(0, 50);
            if (itemUrlInput) itemUrlInput.value = id;
            break;
            
        case 'menu':
			if (itemIdInput) itemIdInput.value=id;
			if (itemUrlInput) itemUrlInput.value = '#';
            if (itemTargetMenuSelect) itemTargetMenuSelect.value = id;
            break;
            
        default:
            // Remettre le formulaire dans l'état initial
            itemTypeSelect.value = '';
			if (itemIdInput) itemIdInput.value='';
            if (itemLabelInput) itemLabelInput.value = '';
            if (itemUrlInput) itemUrlInput.value = '';
            if (itemTargetMenuSelect) itemTargetMenuSelect.value = '';
            toggleFormGroups(); // Masquer tous les groupes
            break;
    }
}

/**
 * Fonction pour initialiser les listeners sur les éléments de liste
 */
function initListItemListeners() {
    // Sélectionner tous les éléments de liste avec la classe hoverable-item
    const listItems = document.querySelectorAll('.list-group-item.hoverable-item');
    
    listItems.forEach(function(item) {
        // Ajouter le listener de clic
        item.addEventListener('click', handleListItemClick);
    });
}

/**
 * Initialisation au chargement du DOM
 */
document.addEventListener('DOMContentLoaded', function() {
    const itemTypeSelect = document.getElementById('item_type');
	const itemDynamicCheckbox = document.getElementById('item_dynamic');
	const targetMenuSelect = document.getElementById('item_target_menu');
	const sourceItemInput = document.getElementById('source_id_item');

    if (itemTypeSelect) {
        // Écouter les changements sur le select
        itemTypeSelect.addEventListener('change', toggleFormGroups);
		// Vérifier que l'élément existe avant d'ajouter l'événement
		if (itemDynamicCheckbox) {
			itemDynamicCheckbox.addEventListener('change',updateDynamicCheckbox );
		}
    }
	
	if (targetMenuSelect && sourceItemInput) {
	    // Ajouter un listener pour détecter les changements
	    targetMenuSelect.addEventListener('change', function() {
	        // Copier la valeur sélectionnée dans l'input caché
	        sourceItemInput.value = this.value;
	    });
	}

	/**
	 * Fonction pour gérer le changement de la checkbox dynamique dans le formulaire de modification
	 */
	function updateDynamicCheckboxInModifyForm() {
		const labelGroup = document.getElementById('label-group_modif');
		const itemLabelInput = document.getElementById('item_label_modif');
		
		if (!labelGroup || !itemLabelInput) {
			return;
		}
		
		// Si la checkbox est cochée, le label devient dynamique
		if (this.checked) {
			// Utiliser les fonctions utilitaires existantes
			clearGroup(labelGroup);
			disableElement(itemLabelInput);
		} else {
			// Vider le groupe et réactiver le champ
			clearGroup(labelGroup);
			enableElement(itemLabelInput);
		}
	}
	
	// Utilisation de la délégation d'événements pour gérer la checkbox dynamique
	// Ceci fonctionne même si l'élément n'existe pas encore dans le DOM
	document.addEventListener('change', function(event) {
		if (event.target && event.target.id === 'item_dynamic_modif') {
			updateDynamicCheckboxInModifyForm.call(event.target);
		}
		
		// Gestion du changement du select menu dans le formulaire de modification
		if (event.target && event.target.id === 'item_target_menu_modif') {
			const sourceItemInput = document.getElementById('source_id_item_modif');
			if (sourceItemInput) {
				// Copier la valeur sélectionnée dans l'input caché
				sourceItemInput.value = event.target.value;
			}
		}
	});
	
	// Debug : Récupère tous les boutons avec la classe "addCanvas" pour tracer les événements
	const buttonsCanvas = document.querySelectorAll('.btnAddCanvas');
	 
    // Initialiser les listeners pour les éléments de liste
    initListItemListeners();
});

/**
 * Fonction d'initialisation manuelle (au cas où le DOM serait déjà chargé)
 */
function initMenusSearchbar() {
    toggleFormGroups();
    
    const itemTypeSelect = document.getElementById('item_type');
    if (itemTypeSelect) {
        // Retirer l'ancien listener s'il existe pour éviter les doublons
        itemTypeSelect.removeEventListener('change', toggleFormGroups);
        // Ajouter le nouveau listener
        itemTypeSelect.addEventListener('change', toggleFormGroups);
    }
    
    // Initialiser les listeners pour les éléments de liste
    initListItemListeners();
}

// Export des fonctions pour utilisation externe si nécessaire
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        toggleFormGroups,
        initMenusSearchbar,
        handleListItemClick,
        updateFormFromListItem
    };
}
