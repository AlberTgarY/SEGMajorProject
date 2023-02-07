<template>
  <div id="admin-site-settings">
    <section class="section">
      <div class="custom-content-container">
        <h1 class="title">Site Settings</h1>

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
                  @change="$v.$touch()"
                />
              </div>
              <p class="help is-danger" v-if="!$v.site.name.required">This field is required</p>
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

            <div class="level is-mobile">
              <div class="level-left">
                <div class="level-item">
                  <router-link
                    :to="`/admin/sites/${site.slug}`"
                    class="button is-light"
                  >Cancel</router-link>
                </div>
                <div class="level-item">
                  <button class="button is-danger" @click="deleteSite()">Delete</button>
                </div>
              </div>
              <div class="level-right">
                <button
                  class="button is-success is-medium"
                  @click="save()"
                  :disabled="$v.$anyError || !site.name || !site.slug"
                >Save</button>
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
import LoadingSpinner from "@/components/LoadingSpinner";
import { required } from "vuelidate/lib/validators";
import { slug, siteSlug } from "@/custom-validators";

export default {
  components: {
    LoadingSpinner
  },
  data() {
    return {
      sites: null,
      site: {},
      loading: true,
      slugAlreadyExists: false
    };
  },
  metaInfo: {
    title: 'Site Settings'
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
    onSlugChanged() {
      this.$v.site.slug.$touch();
      this.slugAlreadyExists = false;
    },
    async save() {
      this.$v.$touch();
      if (this.$v.$invalid) {
        return;
      }
      
      // Check if page slug conflicts with an existing page
      const existingSiteSlugs = this.sites
        .filter(s => s.primaryKey !== this.site.primaryKey)
        .map(s => s.slug);
      if (existingSiteSlugs.includes(this.site.slug)) {
        this.slugAlreadyExists = true;
        return;
      }

      this.loading = true;

      await SitesService.updateSite(this.site);

      const currentSlug = this.$route.params.siteSlug;
      if (currentSlug != this.site.slug) {
        this.$router.push(`/admin/sites/${this.site.slug}/settings`);
      }

      this.$emit("siteUpdate", this.site);

      this.loading = false;
    },
    async deleteSite() {
      this.loading = true;
      await SitesService.deleteSite(this.site);
      this.$router.push("/admin/sites");
    }
  },
  async mounted() {
    const siteSlug = this.$route.params.siteSlug;
    this.site = await SitesService.getSite(siteSlug);
    this.sites = await SitesService.getAllSites();
    this.loading = false;
  }
};
</script>

<style lang="scss" scoped>
</style>