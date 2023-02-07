<template>
  <transition name="fade" mode="out-in">
    <router-view />
  </transition>
</template>

<script>
import AppInfoService from '@/services/app-info-service';

export default {
  data() {
    return {
      appInfo: {
        departmentName: ''
      }
    }
  },
  metaInfo() {
    return {
      titleTemplate: `%s | ${this.appInfo.departmentName}`
    }
  },
  async created() {
    this.appInfo = await AppInfoService.getAppInfo();
  }
}
</script>

<style lang="scss">
@import "styles";
</style>