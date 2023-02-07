<template>
  <div class="root">
    <div class="custom-content-container">
      
      <h1 class="title">Introduction</h1>

      <div class="content page-text">
        Biliary Atresia occurs in the newborn and presents with obstructive jaundice within the first few weeks of life.
        <br />
        <br />Most infants will undergo an operation to try and restore their flow of bile and alleviate jaundice (Kasai portoenterostomy).
        <br />
        <br />This is successful in about one third of children who should then go on to lose their jaundice and have no obvious signs of liver disease.
        <br />
        <br />
        It is partially successful in a further third and has no effect on the remainder.
        Liver transplantation may well be required for children in the last two groups.
        The following information is designed to provide health professionals with more information
        when caring for these children following their Kasai operation. The parents have been given
        this information before discharge from King’s College Hospital.
      </div>
      <Bottom-Nav-Buttons v-bind:next="nextPage"></Bottom-Nav-Buttons>
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
      nextPage: new Page("Nutrition", "Nutrition")
    };
  }
};
</script>

<style lang="scss">
</style>