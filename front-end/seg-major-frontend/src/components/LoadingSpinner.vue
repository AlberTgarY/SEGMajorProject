<template>
  <div class="loading-spinner has-text-centered">
    <font-awesome-icon icon="circle-notch" size="2x" spin title="Loading..." />
  </div>
</template>

<script>
export default {
  name: "LoadingSpinner",
};
</script>

<style lang="scss" scoped>
.loading-spinner {
  width: 100%;
  height: 100%;
  color: #888;
}
</style>
