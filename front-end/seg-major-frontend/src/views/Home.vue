<template>
  <div id="home">
    <section class="hero is-primary">
      <div class="hero-body">
        <div class="container">
          <NHS-logo class="nhs-logo" />
          <h1 class="title">{{ appInfo.departmentName }}</h1>
          <h2 class="subtitle is-4">{{ appInfo.hospitalName }}</h2>
        </div>
      </div>
    </section>

    <div class="section">
      <div class="container">
        <div class="columns">
          <div class="column">
            <p class="title is-3">Sites</p>
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

          <div class="column is-narrow">
            <bulletin-board />
          </div>

          
        </div>  
      </div>
    </div>
     <div class="footer" style="width: 100%;">           
            <p>{{ appInfo.contactDetails }}</p>
            <p>Site editors can <a href="/admin">
            log in here</a>.</p>
          </div>
  </div>
</template>

<script>
import BulletinBoard from "@/components/BulletinBoard.vue";
import SitesService from "@/services/sites-service";
import AppInfoService from "@/services/app-info-service";
import LoadingSpinner from "@/components/LoadingSpinner";
import NHSLogo from "@/components/NHSLogo";

export default {
  name: "Home",
  components: {
    BulletinBoard: BulletinBoard,
    LoadingSpinner,
    NHSLogo,
  },
  data() {
    return {
      sites: null,
      appInfo: {
        hospitalName: '',
        departmentName: '',
        contactDetails: ''
      },
    };
  },
  metaInfo: {
    title: 'Home'
  },
  async created() {
    await Promise.all([
      AppInfoService.getAppInfo().then(value => this.appInfo = value),
      SitesService.getAllSites().then(value => this.sites = value),
    ]);
  }
};
</script>

<style lang="scss" scoped>
.hero {
  .title {
    min-height: 36px;
  }

  .subtitle {
    min-height: 30px;
  }

  .nhs-logo {
    height: 40px;
    margin-bottom: 1rem;
  }
}

.site-card {
  transition: background-color 0.2s;
  &:hover {
    background-color: #f8f8f8;
  }
}

.footer {
  text-align: center;
  padding: 50px;
}
</style>
