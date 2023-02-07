<template>
  <div style="margin-bottom: 25px">
    <div class="notification" style="margin-bottom: 0;" :class="color">
      <div ref="content" :contenteditable="view.editable.toString()"></div>
    </div>
    <div class="card" v-if="view.editable" contenteditable="false">
      <div class="card-content">
        <div class="buttons">
          <button
            class="button is-light rte-menu-button"
            :class="{ 'is-active': !this.color }"
            @click="setColor('')"
          >Grey</button>

          <button
            class="button is-light rte-menu-button"
            :class="{ 'is-active': this.color=='is-danger' }"
            @click="setColor('is-danger')"
          >Red</button>
          <button
            class="button is-light rte-menu-button"
            :class="{ 'is-active': this.color=='is-success' }"
            @click="setColor('is-success')"
          >Green</button>
          <button
            class="button is-light rte-menu-button"
            :class="{ 'is-active': this.color=='is-info' }"
            @click="setColor('is-info')"
          >Blue</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ["node", "updateAttrs", "view"],
  methods: {
    setColor(className) {
      this.updateAttrs({
        color: className
      });
    }
  },
  computed: {
    color: {
      get() {
        return this.node.attrs.color;
      },
      set(color) {
        this.updateAttrs({
          color
        });
      }
    }
  }
};
</script>

<style scoped>

.card-content {
  padding: 10px;
}

.button {
  font-size: 14px;
}
</style>