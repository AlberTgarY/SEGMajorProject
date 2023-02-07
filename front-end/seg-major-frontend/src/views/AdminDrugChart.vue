<template>
  <div id="admin-all-pages">
    <section class="section">
      <div class="custom-content-container">
        <h1 class="title">Drug Chart Configuration</h1>
        <admin-drug-list />
      </div>
    </section>
  </div>
</template>

<script type="text/javascript">
import AdminDrugList from "@/components/AdminDrugList.vue"

export default {
  components: {
    AdminDrugList,
  },
  metaInfo: {
    title: 'Drug Chart Configuration'
  },
}
</script>

<style lang="scss" scoped>
</style>
