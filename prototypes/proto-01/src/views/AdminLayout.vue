<template>
  <div class="root">
    <div class="flex-wrapper">
      <div class="sidebar is-hidden-mobile">
        <div class="section">
          <div class="container">
            <div class="level is-mobile is-marginless">
              <div class="level-left">
                <p class="sidebar-label">Site</p>
              </div>
              <div class="level-right">
                <button class="button is-text">View All</button>
              </div>
            </div>
            <p style="font-size: 20px">Biliary Atresia</p>

            <hr />

            <nav class="sidebar-navigation">
              <div class="navigation-items">
                <router-link to="/admin" class="navigation-item is-unselectable">Dashboard</router-link>

                <router-link to="/admin/pages" class="navigation-item is-unselectable">Pages</router-link>

                <router-link
                  to="/admin/drug-chart"
                  class="navigation-item is-unselectable"
                >Drug Chart</router-link>
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