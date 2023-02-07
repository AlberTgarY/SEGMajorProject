<template>
  <div class="root">
    <div class="custom-content-container">
      
      <h1 class="title">Nutrition</h1>

      <div class="content page-text">
        Malabsorption is present in all cases but gradually resolves over time if good bile flow is
        established.
        <br />
        <br />
        <h5>Recommendations</h5>
        <ul>
          <li>Regular weighing (weekly in first instance) to establish satisfactory growth.</li>
          <li>
            Inform the CNS team or liver dieticians if the child starts falling off their growth
            centiles.
          </li>
          <li>Consider nasogastric feeding if failure to tolerate oral feeding or is not thriving.</li>
          <li>
            <b>Heparon Junior®</b> is the preferred milk formula (MCT-based) for infants with BA.
          </li>
          <li>Weaning should proceed normally. No specific foods are contra-indicated.</li>
        </ul>

        <br />Further advice on dietary management can be obtained by contacting our paediatric
        dieticians on 020 3299 4434 or via switchboard, Pager 07659 114876 call sign 2611
      </div>
      <Bottom-Nav-Buttons v-bind:previous="previousPage" v-bind:next="nextPage"></Bottom-Nav-Buttons>
    </div>
  </div>
</template>

<script>
import BottomNavButtons from "@/components/BottomNavButtons.vue";
import Page from "@/models/Page.model";

export default {
  components: {
    BottomNavButtons: BottomNavButtons
  },
  data() {
    return {
      previousPage: new Page("/", "Introduction"),
      nextPage: new Page("Medications", "Medications")
    };
  }
};
</script>

<style lang="scss">
</style>