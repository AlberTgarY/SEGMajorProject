<template>
  <div id="root">
    <section class="section">
      <div class="custom-content-container">
        <h1 class="title">All Pages</h1>

        <div class="pages-list">
          <router-link to="Introduction" append class="card-wrapper">
            <div class="card">
              <div class="card-content">
                <p class="page-name">Introduction</p>
              </div>
            </div>
          </router-link>

          <router-link to="Nutrition" append class="card-wrapper">
            <div class="card">
              <div class="card-content">
                <p class="page-name">Nutrition</p>
              </div>
            </div>
          </router-link>

          <router-link to="Medications" append class="card-wrapper">
            <div class="card">
              <div class="card-content">
                <p class="page-name">Medications</p>
              </div>
            </div>
          </router-link>

          <router-link to="Immunisation and Infection" append class="card-wrapper">
            <div class="card">
              <div class="card-content">
                <p class="page-name">Immunisation and Infection</p>
              </div>
            </div>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
@import "@/variables";

#root {
  height: 100%;
  background: #fefeff;
}

.pages-list {
  margin-top: 50px;
}

.card-wrapper {
  display: block;
  margin-bottom: 15px;
}

.card {
  transition: background-color 0.2s;

  .page-name {
    font-size: 18px;
    font-weight: bold;
  }

  &:hover {
    background-color: lighten($grey-primary, 23%);
  }
}
</style>

<script>
export default {
  components: {}
};
</script>
