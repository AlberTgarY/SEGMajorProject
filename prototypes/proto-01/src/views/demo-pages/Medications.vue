<template>
  <div class="root">
    <div class="custom-content-container">
      <h1 class="title">Medications</h1>

      <div class="content page-text">
        <ul>
          <li>
            <b>Prophylactic antibiotics</b> – these infants are at risk of cholangitis following surgery.
          </li>
          <li>
            <b>Cephalexin</b> (dose b.d) for 1 month post-operatively.
          </li>

          <li>
            <b>Phenobarbitone</b> (initial 15 mg o.d, increasing as tolerated, to a maximum dose of
            45mgs o.d) is given to help promote bile flow. This can have the effect of making the
            child drowsy we suggest giving the dose in the evening.
          </li>

          <li>
            <b>Ursodeoxycholic Acid</b> is given to promote bile flow and sometimes to control
            pruritus. Dose given twice a day.
          </li>

          <li>
            Vitamin supplements
            <ul>
              <li>
                <b>Dalivit ®</b> 0.6mls and Vitamin E 100mg daily.
              </li>
              <li>
                <b>Vitamin K</b> 1 mg daily (given orally using phytomenadione im/iv preparation).
              </li>
              <li>
                <b>Vitamin D</b> (30,000 IU IM) and
                <b>Vitamin E</b> (10mg/kg IM) are given prior to
                discharge from the ward.
                <b>Vitamin D</b> injection should be repeated monthly
                for 3 months. Vitamin levels will be checked when the child is reviewed in
                clinic at Kings. Some children may require additional oral Vitamin D
                supplementation longer term.
              </li>
            </ul>
          </li>
        </ul>
        <br />

        <div class="notification">
          <b style="color: #e31717;">AVOID:</b> medications with gastrointestinal irritation e.g. Ibuprofen. Paracetamol is a
          perfectly acceptable alternative and safe in the recommended doses
        </div>
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
      previousPage: new Page("Nutrition", "Nutrition"),
      nextPage: new Page("", "Immunisation and Infection")
    };
  }
};
</script>

<style lang="scss">
</style>