<template>
  <div id="admin-all-pages">
    <section class="section">
      <div class="custom-content-container">
        <h1 class="title">All Pages</h1>

        <transition name="fade" mode="out-in">
          <loading-spinner v-if="!pages" style="margin-top: 50px;" />
          <div v-else class="pages-list">
            <div v-if="pages.length == 0" class="notification has-text-centered">
              <h3 class="title is-5">There are no pages to show here.</h3>
              <p
                class="subtitle is-6"
              >You can create a new page using the add button in the bottom left!</p>
            </div>

            <div
              class="notification is-italic is-white is-paddingless has-text-dark"
              v-if="pages.length > 1"
            >
              <strong>Click</strong> to view/edit or
              <strong>drag</strong> to re-order.
            </div>

            <draggable v-model="pages" animation="180" @end="dragEnd()">
              <router-link
                class="is-block"
                :to="page.slug"
                append
                v-for="page of pages"
                :key="page.primaryKey"
              >
                <div class="card">
                  <div class="card-content">
                    <p class="page-name">{{ page.title }}</p>
                  </div>
                </div>
              </router-link>
            </draggable>
          </div>
        </transition>
      </div>
    </section>

    <router-link to="new" append class="button is-primary floating-add-button">
      <i class="material-icons" style="font-size: 48px;">add</i>
    </router-link>
  </div>
</template>

<script>
import SitesService from "@/services/sites-service";
import LoadingSpinner from "@/components/LoadingSpinner";
import draggable from "vuedraggable";

export default {
  components: {
    LoadingSpinner,
    draggable
  },
  data() {
    return {
      pages: null
    };
  },
  metaInfo: {
    title: 'All Pages'
  },
  methods: {
    dragEnd() {
      // update page indexes
      const updatedPages = this.pages.map((p, index) => {
        p.index = index;
        return p;
      });
      SitesService.updatePageIndexes(updatedPages);
    }
  },
  async mounted() {
    const siteSlug = this.$route.params.siteSlug;
    this.pages = await SitesService.getAllPages(siteSlug);
  }
};
</script>

<style lang="scss" scoped>
#admin-all-pages {
  height: 100%;
}

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