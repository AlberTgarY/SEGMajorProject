import { Image as BaseImage } from 'tiptap-extensions'

import ImageComponent from './ImageComponent'

export default class Image extends BaseImage {

  get name() {
    return 'image'
  }

  get schema() {
    return {
      inline: false,
      attrs: {
        src: {},
        alt: {
          default: null,
        },
        title: {
          default: null,
        },
        maxWidth: {
          default: null
        }
      },
      group: 'block',
      content: '',
      draggable: true,
      selectable: false,
      parseDOM: [
        {
          tag: 'img[src]',
          getAttrs: dom => ({
            src: dom.getAttribute('src'),
            title: dom.getAttribute('title'),
            alt: dom.getAttribute('alt'),
            maxWidth: dom.getAttribute('maxWidth'),
          }),
        },
      ],
      toDOM: node => ['img', node.attrs],
    }
  }

  get view() {
    return ImageComponent;
  }
}