<template>
  <div id="root">
    <section class="section">
      <div class="custom-content-container">
        <nav class="breadcrumb is-right" aria-label="breadcrumbs">
          <ul>
            <li>
              <a href="#">Sites</a>
            </li>
            <li>
              <a href="#">Biliary Atresia</a>
            </li>
            <li>
              <a href="#">Pages</a>
            </li>
            <li class="is-active">
              <a href="#" aria-current="page">Page Editor</a>
            </li>
          </ul>
        </nav>

        <input
          type="text"
          class="input title"
          v-bind:value="$route.params.id"
          placeholder="Enter page title here..."
        />

        <editor-menu-bar :editor="editor" v-slot="{ commands, isActive }">
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
              :class="{ 'is-active': isActive.heading({ level: 1 }) }"
              @click="commands.heading({ level: 1 })"
            >
              <span class="rte-menu-button-text-icon">H1</span>
            </button>

            <button
              class="button is-light rte-menu-button"
              :class="{ 'is-active': isActive.heading({ level: 2 }) }"
              @click="commands.heading({ level: 2 })"
            >
              <span class="rte-menu-button-text-icon">H2</span>
            </button>

            <button
              class="button is-light rte-menu-button"
              :class="{ 'is-active': isActive.heading({ level: 3 }) }"
              @click="commands.heading({ level: 3 })"
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
              :class="{ 'is-active': isActive.code_block() }"
              @click="commands.code_block"
            >
              <i class="material-icons">code</i>
            </button>

            <button class="button is-light rte-menu-button" @click="commands.horizontal_rule">-</button>

            <button class="button is-light rte-menu-button" @click="commands.undo">
              <i class="material-icons">undo</i>
            </button>

            <button class="button is-light rte-menu-button" @click="commands.redo">
              <i class="material-icons">redo</i>
            </button>
          </div>
        </editor-menu-bar>
        <editor-content class="content fullheight-editor" :editor="editor" />

        <div class="buttons" style="justify-content: end">
          <button class="button is-light">Cancel</button>
          <button class="button is-danger">Delete</button>
          <button class="button is-success">Save</button>
        </div>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
#root {
  display: flex;
  height: 100%;
  flex-direction: column;
}

.section {
  flex-grow: 1;
}

.custom-content-container {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  height: 100%;
}

.rich-text-editor-menu {
  margin-bottom: 10px;
}

.rte-menu-button {
  padding: 0 10px;
  height: 35px;
  min-width: 42px;
  margin-right: 5px;
  background-color: transparent;
  border: 2px solid #f5f5f5;

  .rte-menu-button-text-icon {
    font-size: 18px;
    font-weight: bold;
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
  CodeBlock,
  HorizontalRule,
  History,
  TrailingNode
} from "tiptap-extensions";

export default {
  components: {
    EditorMenuBar,
    EditorContent
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
          new CodeBlock(),
          new HorizontalRule(),
          new History(),
          new TrailingNode({
            node: "paragraph",
            notAfter: ["paragraph"]
          })
        ]
      }),
      newPage: Boolean
    };
  },
  beforeDestroy() {
    this.editor.destroy();
  }
};
</script>