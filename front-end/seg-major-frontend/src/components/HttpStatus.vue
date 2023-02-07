<template>
  <div class="http-status" v-if="statusContent">
    <div class="has-text-centered">
      <h1 class="title is-1">{{ statusContent.title }}</h1>
      <h2 class="subtitle is-3">{{ statusContent.subtitle }}</h2>
      <p class="subtitle is-5">{{ statusContent.paragraph }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "HttpStatus",
  props: {
    httpStatusCode: {
      type: Number,
      required: true,
      validator: function (value) {
        return value >= 100 && value < 600
      }
    }
  },
  metaInfo() {
    return {
      meta: this.statusContent?.meta ?? []
    }
  },
  watch: {
    httpStatusCode: {
      immediate: true,
      handler(val) {
        this.statusContent = this.statusContentMap[val];
        this.$meta().refresh();
      }
    }
  },
  data() {
    return {
      statusContent: undefined,
      statusContentMap: {
        401: { title: '401', subtitle: 'Unauthorized', paragraph: 'You are not authorized to view this page.' },
        404: { title: '404', subtitle: 'Page not found', paragraph: 'Sorry, the page you were looking for does not exist.', meta: [{ vmid: 'robots', name: 'robots', content: 'noindex' }] },
        500: { title: '500', subtitle: 'Internal server error', paragraph: 'Please try again later.' },
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.http-status {
  width: 100%;
  height: 100%;
}
</style>
