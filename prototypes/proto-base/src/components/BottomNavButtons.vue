<template>
  <div class="level" style="margin-top: 70px;">
    <div class="level-left">
      <div class="level-item">
        <!-- Previous Button-->
        <router-link v-bind:to="previous.slug" v-if="previous">
          <div class="button is-light is-large bottom-nav-btn">
            <i class="material-icons" style="margin-right: 5px">arrow_back_ios</i>
            {{ previous.title }}
          </div>
        </router-link>
      </div>
    </div>
    <div class="level-right">
      <div class="level-item">
        <!-- Next Button -->
        <router-link v-bind:to="next.slug" v-if="next">
          <div class="button is-light is-large bottom-nav-btn">
            {{ next.title }}
            <i
              class="material-icons"
              style="margin-left: 12px; margin-right: -3px;"
            >arrow_forward_ios</i>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import Page from "@/models/Page.model";
export default {
  name: "BottomNavButtons",
  props: {
    previous: Page,
    next: Page
  }
};
</script>

<style lang="scss">
.bottom-nav-btn {
  transition: background-color 0.12s, color 0.12s;
  font-size: 20px;
  i {
    font-size: 18px;
  }
  &:hover {
    color: #222;
  }
}
</style>