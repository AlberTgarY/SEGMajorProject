<template>
  <div id="drug-chart-creator">
    <Navbar />
    <section class="hero is-primary">
      <div class="hero-body">
        <div class="container">
          <h1 class="title">Drug Chart Creator</h1>
          <h2 class="subtitle">Create your own printable drug chart</h2>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <transition name="fade-transform" mode="out-in">
          <drug-chart-layout />
        </transition>
      </div>
    </section>
  </div>
</template>

<script>
import DrugChartLayout from "@/components/DrugChartLayout.vue";
import Navbar from "@/components/Navbar.vue";

export default {
  name: "DrugChartCreator",
  components: {
    DrugChartLayout,
    Navbar
  },
  metaInfo: {
    title: 'Drug Chart Creator'
  },
}
</script>