<template>
  <div id="admin-new-site">
    <section class="section">
      <div class="custom-content-container">
        <h1 class="title">Create New Site</h1>

        <transition name="fade" mode="out-in">
          <loading-spinner v-if="loading" style="margin-top: 50px;" />
          <div v-else>
            <div class="field">
              <label class="label">Site Name</label>
              <div class="control">
                <input
                  class="input"
                  v-model="site.name"
                  type="text"
                  placeholder="Enter site name..."
                  @change="$v.site.name.$touch()"
                />
              </div>
              <div v-if="$v.site.name.$dirty">
                <p class="help is-danger" v-if="!$v.site.name.required">This field is required</p>
              </div>
            </div>

            <label class="label">URL Slug</label>
            <div class="field">
              <p class="control is-expanded">
                <input
                  class="input"
                  type="text"
                  v-model="site.slug"
                  placeholder="Enter URL Slug here..."
                  @input="onSlugChanged()"
                />
              </p>

              <div v-if="$v.site.slug.$dirty">
                <p class="help is-danger" v-if="!$v.site.slug.required">This field is required</p>
                <p
                  class="help is-danger"
                  v-else-if="!$v.site.slug.slug"
                >Slug can only contain lowercase letters, numbers, and hyphens</p>
                <p
                  class="help is-danger"
                  v-else-if="!$v.site.slug.siteSlug"
                >This slug is not allowed because it is reserved</p>
                <p
                  class="help is-danger"
                  v-else-if="slugAlreadyExists"
                >This slug is already in use by another site</p>
              </div>
            </div>

            <div class="level is-mobile">
              <div class="level-left">
                <router-link to="/admin/sites" class="button is-light">Cancel</router-link>
              </div>
              <div class="level-right">
                <button class="button is-success is-medium" @click="createSite()">Create</button>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </section>
  </div>
</template>

<script>
import SitesService from "@/services/sites-service";

import { required } from "vuelidate/lib/validators";
import { slug, siteSlug } from "@/custom-validators";

import LoadingSpinner from "@/components/LoadingSpinner";

export default {
  components: {
    LoadingSpinner
  },
  data() {
    return {
      sites: null,
      site: {},
      slugAlreadyExists: false,
      loading: true
    };
  },
  metaInfo: {
    title: 'New Site'
  },
  validations: {
    site: {
      name: {
        required
      },
      slug: {
        required,
        slug,
        siteSlug
      }
    }
  },
  methods: {
    async createSite() {
      this.$v.$touch();
      if (this.$v.$invalid) {
        return;
      }
      
      // Check if page slug conflicts with an existing page
      const existingSiteSlugs = this.sites.map(s => s.slug);
      if (existingSiteSlugs.includes(this.site.slug)) {
        this.slugAlreadyExists = true;
        return;
      }
      await SitesService.createSite(this.site);
      this.$router.push(`/admin/sites/${this.site.slug}`);
    },
    onSlugChanged() {
      this.$v.site.slug.$touch();
      this.slugAlreadyExists = false;
    }
  },
  async mounted() {
    this.sites = await SitesService.getAllSites();
    this.loading = false;
  }
};
</script>

<style>
</style>