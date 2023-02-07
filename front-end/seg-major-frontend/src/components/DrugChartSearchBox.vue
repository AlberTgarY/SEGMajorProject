<template>
  <div class="field">
    <label class="label">Medicine</label>
    <div class="control">
      <input class="input search" type="text" v-model="search" placeholder="Search medicines">
      <div class="select is-multiple is-fullwidth">
        <select class="medicine-list" name="users-out" size="10" v-model="selectedDrugName">
          <option v-for="medicine in searchResults" :key="medicine.primaryKey" :value="medicine.name" class="list-group-item">{{ medicine.name }}</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script>
import medicineService from '../services/medicine-service.js'

export default {
  name: "DrugChartSearchBox",
  data: function() { 
    return {
      medicines: [],
      search: "",
      selectedDrugName: "",
    }
  },
  async mounted() {
    this.medicines = await medicineService.getAllMedicines();
  },
  computed: {
    searchResults: function() {
      const query = this.search.toLowerCase();
      return this.medicines.filter(medicine => medicine.name.toLowerCase().includes(query));
    }
  },
};
</script>

<style lang="scss" scoped>
.search {
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

.medicine-list {
  min-height: 350px;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}
</style>
