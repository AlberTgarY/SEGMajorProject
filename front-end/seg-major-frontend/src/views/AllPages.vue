<template>
  <div id="all-pages">
    <section class="section">
      <transition name="fade" mode="out-in">
        <loading-spinner v-if="!pages" style="margin-top: 50px;" />
        <div v-else-if="pages.length == 0" class="has-text-dark has-text-centered">
          <div style="margin-bottom: 1rem;">
            <font-awesome-icon :icon="['far', 'frown']" size="3x" />
          </div>No pages found
        </div>

        <div class="custom-content-container" v-else>
          <h1 class="title">{{ siteName }}</h1>

          <div class="pages-list">
            <div v-if="pages.length == 0" class="notification has-text-centered">
              <h3 class="title is-5">There are no pages to show here.</h3>
              <p
                class="subtitle is-6"
              >You can create a new page using the add button in the bottom left!</p>
            </div>
            <router-link
              :to="page.slug"
              v-for="page of pages"
              :key="page.primaryKey"
            >
              <div class="card">
                <div class="card-content">
                  <p class="page-name">{{ page.title }}</p>
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </transition>
    </section>
  </div>
</template>

<script>
import LoadingSpinner from "@/components/LoadingSpinner";

export default {
  components: {
    LoadingSpinner
  },
  props: {
    pages: {
      type: Array
    }
  },
  computed: {
    siteName() {
      if (this.pages && this.pages.length > 0) {
        return this.pages[0].site.name;
      } else {
        return null;
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.pages-list {
  margin-top: 50px;
  .card {
    margin-bottom: 20px;

    .page-name {
      font-size: 18px;
      font-weight: bold;
    }
  }
}
</style>