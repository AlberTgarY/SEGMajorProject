<template>
  <div style="margin-bottom: 25px" contenteditable="true">
    <img
      :src="node.attrs.src"
      :alt="node.attrs.alt"
      :title="node.attrs.title"
      :style="`max-width: ${maxWidth}px`"
    />
    <div class="card-container" v-if="view.editable">
      <div class="card">
        <div class="card-content">
          <div class="controls-container">
            <div class="field has-addons is-marginless">
              <p class="control is-marginless">
                <a class="button is-static has-background-white has-text-dark">Max width</a>
              </p>
              <p class="control">
                <input
                  v-model="maxWidth"
                  class="input"
                  type="number"
                  min="50"
                  @blur="onInputBlur()"
                  placeholder="Enter max width..."
                />
              </p>
            </div>
            <p class="optional-text">(optional)</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ["node", "updateAttrs", "view"],
  data() {
    return {
      minWidth: 50
    };
  },
  methods: {
    onInputBlur() {
      if (this.maxWidth < this.minWidth) {
        this.maxWidth = this.minWidth;
      }
    }
  },
  computed: {
    maxWidth: {
      get() {
        return this.node.attrs.maxWidth;
      },
      set(maxWidth) {
        if (maxWidth < 50) return;
        this.updateAttrs({
          maxWidth
        });
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.card {
  display: inline-block;
  .card-content {
    padding: 10px;
  }
}

.controls-container {
  display: flex;
  align-items: center;
  .optional-text {
    color: #888;
    padding-left: 12px;
  }
}
</style>