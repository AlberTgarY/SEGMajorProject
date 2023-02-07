<template>
  <div class="root">
    <div class="flex-wrapper">
      <div class="sidebar is-hidden-mobile">
        <div class="section">
          <div class="container">
            <p class="sidebar-label">Contents</p>
            <nav class="sidebar-navigation">
              <div class="navigation-items">
                <router-link to="/" class="navigation-item is-unselectable">Introduction</router-link>

                <router-link to="Nutrition" class="navigation-item is-unselectable">Nutrition</router-link>

                <router-link to="Medications" class="navigation-item is-unselectable">Medications</router-link>
                <div class="navigation-item is-unselectable">Immunisation and Infection</div>
                <div class="navigation-item is-unselectable">Cholangitis</div>
                <div class="navigation-item is-unselectable">Ascites</div>
                <div class="navigation-item is-unselectable">Portal Hypertension</div>
                <div class="navigation-item is-unselectable">Progression</div>
                <div class="navigation-item is-unselectable">Contacts</div>
              </div>
            </nav>
          </div>
        </div>
      </div>

      <div class="page-content">
        <transition name="fade" mode="out-in">
          <router-view v-bind:key="$route.path"></router-view>
        </transition>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.root {
  height: 100%;
}

.flex-wrapper {
  height: 100%;
  display: flex;
}

.page-content {
  flex-grow: 1;
  overflow-y: scroll;
}
</style>

<script>
export default {};
</script>