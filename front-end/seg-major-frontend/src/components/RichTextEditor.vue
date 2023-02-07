<template>
  <div id="rich-text-editor" :class="{ 'disabled': disabled }">
    <editor-menu-bar v-if="editable" :editor="editor" v-slot="{ commands, isActive }">
      <div class="rich-text-editor-menu">
        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.bold() }"
          @click="commands.bold"
        >
          <i class="material-icons">format_bold</i>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.italic() }"
          @click="commands.italic"
        >
          <i class="material-icons">format_italic</i>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.underline() }"
          @click="commands.underline"
        >
          <i class="material-icons">format_underline</i>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.paragraph() }"
          @click="commands.paragraph"
        >
          <span class="rte-menu-button-text-icon">P</span>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.heading({ level: 2 }) }"
          @click="commands.heading({ level: 2 })"
        >
          <span class="rte-menu-button-text-icon">H1</span>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.heading({ level: 3 }) }"
          @click="commands.heading({ level: 3 })"
        >
          <span class="rte-menu-button-text-icon">H2</span>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.heading({ level: 4 }) }"
          @click="commands.heading({ level: 4 })"
        >
          <span class="rte-menu-button-text-icon">H3</span>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.bullet_list() }"
          @click="commands.bullet_list"
        >
          <i class="material-icons">format_list_bulleted</i>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.ordered_list() }"
          @click="commands.ordered_list"
        >
          <i class="material-icons">format_list_numbered</i>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.blockquote() }"
          @click="commands.blockquote"
        >
          <i class="material-icons">format_quote</i>
        </button>

        <button
          class="button is-light rte-menu-button"
          :class="{ 'is-active': isActive.notification() }"
          @click="commands.notification"
        >
          <i class="material-icons">note</i>
        </button>

        <button class="button is-light rte-menu-button" @click="commands.horizontal_rule">
          <i class="material-icons">remove</i>
        </button>

        <button class="button is-light rte-menu-button" @click="commands.undo">
          <i class="material-icons">undo</i>
        </button>

        <button class="button is-light rte-menu-button" @click="commands.redo">
          <i class="material-icons">redo</i>
        </button>

        <div class="insert-image-bar">
          <i class="material-icons" style="margin-right: 2px; font-size: 18px">image</i>

          <p class="insert-image-bar-text">Insert Image</p>
          <button
            class="button is-light is-small"
            style="margin-right: 12px"
            @click="showImagePrompt(commands.image)"
          >
            <i class="material-icons" style="margin-right: 5px;">link</i>Using URL
          </button>
          <div class="file is-light is-small">
            <label class="file-label">
              <input
                class="file-input"
                type="file"
                accept="image/*"
                ref="file"
                @change="handleFileUpload(commands.image)"
                :disabled="uploadingImage"
              />
              <span class="file-cta">
                <span class="file-icon" style="margin-right: 12px;">
                  <i class="material-icons" v-if="!uploadingImage">cloud_upload</i>
                  <font-awesome-icon v-else icon="circle-notch" size="lg" spin title="Loading..." />
                </span>
                <span class="file-label">Upload image</span>
              </span>
            </label>
          </div>
        </div>
      </div>
    </editor-menu-bar>
    <editor-content class="content fullheight-editor" :editor="editor" />
  </div>
</template>
<style lang="scss" scoped>
#rich-text-editor {
  height: 100%;
  transition: opacity 0.2s;
}

.disabled {
  opacity: 0.68;
  pointer-events: none;
}

.rich-text-editor-menu {
  margin-bottom: 20px;
}

.insert-image-bar {
  display: flex;
  align-items: center;
  color: #555;
  margin-top: 10px;

  .insert-image-bar-text {
    margin-right: 25px;
    margin-left: 5px;
    font-weight: bold;
    font-size: 14px;
    text-transform: uppercase;
  }

  i.material-icons {
    font-size: 18px;
  }
}
</style>

<script>
import { Editor, EditorContent, EditorMenuBar } from "tiptap";
import {
  Placeholder,
  Bold,
  Italic,
  Underline,
  Heading,
  ListItem,
  BulletList,
  OrderedList,
  Blockquote,
  HorizontalRule,
  History,
  TrailingNode
} from "tiptap-extensions";

import Notification from "../components/Notification.js";
import Image from "@/components/Image.js";
import ImagesService from "@/services/images-service";
import { url } from "vuelidate/lib/validators";

export default {
  components: {
    EditorMenuBar,
    EditorContent
  },
  props: {
    editable: {
      type: Boolean,
      default: true
    },
    value: {
      type: String
    },
    disabled: {
      type: Boolean,
      deafult: false
    }
  },
  data() {
    return {
      editor: new Editor({
        extensions: [
          new Placeholder({
            emptyEditorClass: "is-editor-empty",
            emptyNodeClass: "is-empty",
            emptyNodeText: "Start typing page content here ...",
            showOnlyWhenEditable: true,
            showOnlyCurrent: true
          }),
          new Bold(),
          new Italic(),
          new Underline(),
          new Heading(),
          new BulletList(),
          new ListItem(),
          new OrderedList(),
          new Blockquote(),
          new HorizontalRule(),
          new History(),
          new TrailingNode({
            node: "paragraph",
            notAfter: ["paragraph"]
          }),
          new Notification(),
          new Image()
        ],
        editable: this.editable,
        onUpdate: ({getJSON}) => {
          this.$emit("input", JSON.stringify(getJSON()));
        },
        onFocus: () => {
          if (this.disabled) {
            this.editor.blur();
          }
        },
        content: this.value ? JSON.parse(this.value) : null
      }),
      uploadingImage: false
    };
  },
  methods: {
    async handleFileUpload(command) {
      this.file = this.$refs.file.files[0];
      if (this.file.type.split("/")[0] !== "image") {
        alert("Only image files can be uploaded");
        return;
      }

      // if file size is greater than 10MB
      if (this.file.size > 10000000) {
        alert("This file exceeds the maximum file size of 10MB. Please choose a smaller image.");
        return;
      }

      this.uploadingImage = true;
      const res = await ImagesService.uploadImage(this.file);
      this.uploadingImage = false;

      const src = res.config.baseURL + "/images/" + res.data;
      command({ src });
    },
    showImagePrompt(command) {
      const src = prompt("Enter URL of the image you would like to insert");
      if (src !== null) {
        const isValidURL = url(src);
        if (src != "" && isValidURL) {
          command({ src });
        } else {
          alert("Image URL was invalid, please try again.");
        }
      }
    }
  },
  watch: {
    value: function(val) {
      if (val !== JSON.stringify(this.editor.getJSON())) {
        this.editor.setContent(JSON.parse(val));
      }
    }
  },
  beforeDestroy() {
    setTimeout(() => {
      this.editor.destroy();
    }, 200);
  }
};
</script>

<style lang="scss" scoped>
</style>
