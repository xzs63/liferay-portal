import Component from 'metal-component';
import {Config} from 'metal-state';
import Soy from 'metal-soy';

import templates from './SidebarFragmentCollection.soy';

/**
 * SidebarFragmentCollection
 */

class SidebarFragmentCollection extends Component {

	/**
	 * Callback that is executed when a fragment entry is clicked.
	 * It propagates a collectionEntryClick event with the fragment information.
	 * @param {Event} event
	 * @private
	 */

	_handleEntryClick(event) {
		const fragmentEntryId = event.delegateTarget.dataset.fragmentEntryId;
		const fragmentName = this.fragmentCollection.fragmentEntries.find(
			entry => entry.fragmentEntryId === fragmentEntryId
		).name;

		this.emit(
			'collectionEntryClick',
			{
				fragmentEntryId,
				fragmentName
			}
		);
	}
}

/**
 * State definition.
 * @type {!Object}
 * @static
 */

SidebarFragmentCollection.STATE = {

	/**
	 * Available entries that can be dragged inside the existing Page Template,
	 * organized by fragment categories.
	 * @default undefined
	 * @instance
	 * @memberOf SidebarFragmentCollection
	 * @type {!Array<object>}
	 */

	fragmentCollection: Config.shapeOf(
		{
			fragmentCollectionId: Config.string().required(),
			fragmentEntries: Config.arrayOf(
				Config.shapeOf(
					{
						fragmentEntryId: Config.string().required(),
						imagePreviewURL: Config.string(),
						name: Config.string().required()
					}
				).required()
			).required(),
			name: Config.string().required()
		}
	),

	/**
	 * Portlet namespace needed for prefixing form inputs
	 * @default undefined
	 * @instance
	 * @memberOf SidebarFragmentCollection
	 * @type {!string}
	 */

	portletNamespace: Config.string().required(),

	/**
	 * Path of the available icons.
	 * @default undefined
	 * @instance
	 * @memberOf SidebarFragmentCollection
	 * @type {!string}
	 */

	spritemap: Config.string().required()
};

Soy.register(SidebarFragmentCollection, templates);

export {SidebarFragmentCollection};
export default SidebarFragmentCollection;