import { Node } from 'tiptap'
import { toggleWrap } from 'tiptap-commands'

import NotificationComponent from './NotificationComponent'

export default class Notification extends Node {

    get name() {
        return 'notification'
    }

    get schema() {
        return {
            attrs: {
                color: {
                    default: ''
                }
            },
            group: 'block',
            content: 'block+',
            parseDOM: [{
                tag: '.notification',
                getAttrs: dom => ({
                    color: dom.getAttribute('color'),
                }),
            }],
            toDOM: node => ['div', {
                color: node.attrs.color,
            }],
        }
    }

    get view() {
        return NotificationComponent;
    }

    commands({ type, schema }) {
        return () => toggleWrap(type, schema.nodes.paragraph)
    }

}