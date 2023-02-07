<template>
  <div id="page-empty-state">
    <section class="section" v-if="pages">
      <div class="custom-content-container">
        <div class="has-text-dark has-text-centered">
          <div style="margin-bottom: 1rem; margin-top: 2rem">
            <font-awesome-icon :icon="['far', 'frown']" size="3x" />
          </div>No pages found
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  props: {
    pages: {
      type: Array
    }
  },
  watch: {
    pages: {
      immediate: true,
      handler() {
        if (this.pages && this.pages.length > 0) {
          // show the first page automatically
          this.$router.push({ path: this.pages[0].slug, append: true });
        }
      }
    }
  }
};
</script>

<style>
</style>