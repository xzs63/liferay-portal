import 'frontend-taglib/cards_treeview/CardsTreeview.es';
import Component from 'metal-component';
import Soy from 'metal-soy';
import {Config} from 'metal-state';

import templates from './SelectLayout.soy';

/**
 * SelectLayout
 *
 * This component shows a list of available layouts to select in expanded tree
 * and allows to filter them by searching.
 *
 * @review
 */

class SelectLayout extends Component {

	/**
	 * Filters deep nested nodes based on a filtering value
	 *
	 * @type {Array<Object>} nodes
	 * @type {string} filterValue
	 * @private
	 * @review
	 */

	_filterSiblingNodes(nodes, filterValue) {
		let filteredNodes = [];

		nodes.forEach(
			node => {
				if (node.name.toLowerCase().indexOf(filterValue) !== -1) {
					filteredNodes.push(node);
				}

				if (node.children) {
					filteredNodes = filteredNodes.concat(
						this._filterSiblingNodes(node.children, filterValue)
					);
				}
			}
		);

		return filteredNodes;
	}

	/**
	 * Searchs for nodes by name based on a filtering value
	 *
	 * @param {!Event} event
	 * @private
	 * @review
	 */

	_searchNodes(event) {
		if (!this.originalNodes) {
			this.originalNodes = this.nodes;
		}
		else {
			this.nodes = this.originalNodes;
		}

		const filterValue = event.delegateTarget.value.toLowerCase();

		if (filterValue !== '') {
			this.viewType = SelectLayout.VIEW_TYPES.flat;
			this.nodes = this._filterSiblingNodes(this.nodes, filterValue);
		}
		else {
			this.viewType = SelectLayout.VIEW_TYPES.tree;
		}
	}

	/**
	 * Fires item selector save event on selected node change
	 *
	 * @param {!Event} event
	 * @private
	 * @review
	 */

	_selectedNodeChange(event) {
		if (this.multiSelection) {
			Liferay.Util.getOpener().Liferay.fire(
				this.itemSelectorSaveEvent,
				{
					data: event.newVal
				}
			);
		}
		else {
			const node = event.newVal[0];

			if (node) {
				if (this.followURLOnTitleClick) {
					Liferay.Util.getOpener().document.location.href = node.url;
				}
				else {
					const data = {
						groupId: node.groupId,
						id: node.id,
						layoutId: node.layoutId,
						name: node.value,
						privateLayout: node.privateLayout,
						value: node.url
					};

					Liferay.Util.getOpener().Liferay.fire(
						this.itemSelectorSaveEvent,
						{
							data: data
						}
					);
				}
			}
		}
	}

	/**
	 * Prevent form submission, as filter process is automatically
	 * done on keypress
	 * @param {!Event} event
	 * @private
	 * @review
	 */

	_handleSearchFormSubmit(event) {
		event.preventDefault();
	}
}

/**
 * SelectLayout view types
 * @review
 * @static
 * @type {Object}
 */

SelectLayout.VIEW_TYPES = {
	flat: 'flat',
	tree: 'tree'
};

/**
 * State definition.
 * @review
 * @static
 * @type {!Object}
 */

SelectLayout.STATE = {

	/**
	 * Enables URL following on the title click
	 * @default false
	 * @instance
	 * @memberOf SelectLayout
	 * @review
	 * @type {boolean}
	 */

	followURLOnTitleClick: Config.bool().value(false),

	/**
	 * Event name to fire on node selection
	 * @default ''
	 * @instance
	 * @memberOf SelectLayout
	 * @review
	 * @type {string}
	 */

	itemSelectorSaveEvent: Config.string().value(''),

	/**
	 * List of nodes
	 * @default undefined
	 * @instance
	 * @memberOf SelectLayout
	 * @review
	 * @type {!Array<Object>}
	 */

	nodes: Config.array().required(),

	/**
	 * Enables multiple selection of tree elements
	 * @default false
	 * @instance
	 * @memberOf SelectLayout
	 * @review
	 * @type {boolean}
	 */

	multiSelection: Config.bool().value(false),

	/**
	 * Theme images root path
	 * @default undefined
	 * @instance
	 * @memberOf SelectLayout
	 * @review
	 * @type {!string}
	 */

	pathThemeImages: Config.string().required(),

	/**
	 * Type of view to render. Accepted values are defined inside
	 * SelectLayout.VIEW_TYPES static property.
	 * @default SelectLayout.VIEW_TYPES.tree
	 * @instance
	 * @memberOf SelectLayout
	 * @review
	 * @type {string}
	 */

	viewType: Config
		.oneOf(Object.values(SelectLayout.VIEW_TYPES))
		.value(SelectLayout.VIEW_TYPES.tree)
};

Soy.register(SelectLayout, templates);

export {SelectLayout};
export default SelectLayout;