<template>
  <div id="admin-site-layout">
    <div class="flex-wrapper">
      <div class="sidebar is-hidden-mobile">
        <div class="section">
          <div class="level is-mobile is-marginless">
            <div class="level-left">
              <p class="sidebar-label">Site</p>
            </div>
            <div class="level-right">
              <router-link to="/admin/sites" class="button is-text">View All</router-link>
            </div>
          </div>
          <p style="font-size: 20px">{{ this.site.name }}</p>

          <hr />

          <nav class="sidebar-navigation">
            <div class="navigation-items">
              <router-link
                :to="`/admin/sites/${this.site.slug}/pages`"
                class="navigation-item is-unselectable"
              >Pages</router-link>

              <router-link
                :to="`/admin/sites/${this.site.slug}/settings`"
                class="navigation-item is-unselectable"
              >Site Settings</router-link>
            </div>
          </nav>
        </div>
      </div>

      <div class="expanded-scrollable-area">
      <transition name="fade" mode="out-in" :duration="{ leave: 50 }">

        <router-view @siteUpdate="setSite($event)" />
      </transition>
      </div>
    </div>
  </div>
</template>

<script>
import SitesService from "@/services/sites-service";

export default {
  data() {
    return {
      site: { name: "..." }
    };
  },
  methods: {
    setSite(site) {
      this.site = Object.assign({}, site);
    }
  },
  async mounted() {
    const siteSlug = this.$route.params.siteSlug;
    this.site = await SitesService.getSite(siteSlug);
  }
};
</script>

<style lang="scss" scoped>
#admin-site-layout {
  height: 100%;
}

.flex-wrapper {
  height: 100%;
  display: flex;
}
</style>