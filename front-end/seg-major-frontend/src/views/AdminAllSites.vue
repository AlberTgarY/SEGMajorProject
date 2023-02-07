<template>
  <div id="admin-all-sites">
    <div class="expanded-scrollable-area">
      <section class="section">
        <div class="custom-content-container">
          <h1 class="title">All Sites</h1>
          <transition name="fade" mode="out-in">
            <loading-spinner v-if="!sites" style="margin-top: 50px;" />
            <div v-else>
              <router-link
                v-for="site of sites"
                :key="site.primaryKey"
                class="is-block"
                style="margin-bottom: 20px"
                :to="site.slug"
                append
              >
                <div class="card">
                  <div class="card-content">
                    <h5 class="title is-5">{{ site.name }}</h5>
                  </div>
                </div>
              </router-link>
            </div>
          </transition>
        </div>
      </section>
    </div>

    <router-link to="new" append class="button is-primary floating-add-button">
      <i class="material-icons" style="font-size: 48px;">add</i>
    </router-link>
  </div>
</template>

<script>
import SitesService from "@/services/sites-service";
import LoadingSpinner from "@/components/LoadingSpinner";

export default {
  components: {
    LoadingSpinner
  },
  data() {
    return {
      sites: null
    };
  },
  metaInfo: {
    title: 'Sites'
  },
  async mounted() {
    this.sites = await SitesService.getAllSites();
  }
};
</script>

<style lang="scss" scoped>
#admin-all-sites {
  height: 100%;
}

.expanded-scrollable-area {
  overflow-y: scroll;
}
</style>