<template>
  <div id="drug-list">
    <div class="columns">
      <div class="column">
        <div class="field">
          <div class="control">
            <input class="input search" type="text" v-model="search" placeholder="Search">
            <div class="select is-multiple is-fullwidth">
              <select name="users-out" id="all-drugs" multiple="multiple" size="10">
                <option type="button" @click="changeInfo(medicine)" v-for="medicine in searchResults" :key="medicine.primaryKey" class="list-group-item">{{ medicine.name }}</option>
              </select>
            </div>
          </div>
        </div>
        <button class="button is-fullwidth is-primary" type="button" @click="addInfo()">Add</button>
      </div>
      <div class="column">
        <div v-if="drugBox">
          <h2 v-if="addingDrug" class="title is-4">Create New Drug</h2>

          <div v-if="changingDrug" class="field">
            <label class="label">Name</label>
            <input v-model="selectedMedicine.name" class="input is-fullwidth" />
          </div>

          <div v-if="addingDrug" class="field">
            <label class="label">Name</label>
            <input class="input is-fullwidth" id="add_name" />
          </div>

          <div class="field" v-if="addingDrug || changingDrug">
            <label class="label">Type</label>
            <multiselect v-model="selected" class="is-fullwidth" placeholder="Select" label="title" track-by="title" :options="options" :option-height="104" :show-labels="false">
              <template slot="option" slot-scope="props">
                <div class="level">
                  <img class="option__image level-left" :src="props.option.img">
                  <div class="option__desc level-right"><span class="option__title">{{ props.option.title }}</span><span class="option__small">{{ props.option.desc }}</span></div>
                </div>
              </template>
            </multiselect>
          </div>

          <div v-if="changingDrug" class="buttons">
            <button v-if="showSaveButton" class="button is-success" :class="{ 'is-loading': isSaving }" @click="updateDrug()" id="saveButton">Save</button>
            <button v-if="showDeleteButton" class="button is-danger" :class="{ 'is-loading': isDeleting }" @click="deleteDrug()" id="deleteButton">Delete</button>
          </div>
          <div v-if="addingDrug" class="buttons">
            <button class="button is-success" :class="{ 'is-loading': isSaving }" @click="addDrug()" id="addDrug">Save</button>
            <button class="button is-light" @click="addingDrug = false">Cancel</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script type="text/javascript">
import Multiselect from 'vue-multiselect'
import MedicineService from '../services/medicine-service.js'

export default {
  name: "AdminDrugList",
  components: { 
    Multiselect,
  },
  data() { 
    return {
      selectedMedicine: { id: '', name: '', type: '' },
      search: "",
      drugBox: false,
      changingDrug: false,
      addingDrug: false,
      showTable: false,
      showDeleteButton: false,
      showSaveButton: false,
      isSaving: false,
      isDeleting: false,
      sortType: 'sort',
      selected: { title: '', desc: '', img: '' },
      //list of all types
      options: [
        { title: 'Liquid', img: require("../assets/drug-types/liquid.png") },
        { title: 'Tablets', img: require("../assets/drug-types/tablets.png") },
        { title: 'Capsules', img: require("../assets/drug-types/capsules.png")},
        { title: 'Topical', img: require("../assets/drug-types/topical.png")},
        { title: 'Suppositories', img: require("../assets/drug-types/suppositories.png")},
        { title: 'Drops', img: require("../assets/drug-types/drops.png") },
        { title: 'Inhalers', img: require("../assets/drug-types/inhalers.png") },
        { title: 'Injections', img: require("../assets/drug-types/injection.png") },
        { title: "Implants/Patches", img: require("../assets/drug-types/implants.png") },
      ],
      medicines: [
        //list of all medicines
      ]
    }
  },
  async mounted() {
    /**
    * fetch the drug list
    */
    this.medicines = await MedicineService.getAllMedicines();
  },
  computed: {
    /**
    * search function
    */
    searchResults: function() {
      const query = this.search.toLowerCase();
      return this.medicines.filter(medicine => medicine.name.toLowerCase().includes(query));
    }
  },
  methods: {
    /**
    * @param {Object} matching string
    * match if theres invalid word
    */
    regTest: function(string) {
      let reg = /[@#%&+;":{}'*^!.,~_=><]+/g
      return reg.test(string)
    },
    //add drug to db
    addDrug: async function() {
      this.isSaving = true
      const name = document.getElementById('add_name').value
      const type = this.selected.title
      
      if (name !== "" && type !== "") {
        if (name.length > 40) {
          window.alert("Medicine name cannot be longer than 40 characters.")
        } else {
          if (this.regTest(name)) {
            window.alert("Medicine name contains invalid characters.")
          } else {
            var data = { "name": name, "type": type }
            await MedicineService.createMedicine(data);
            location.reload();
            return;
          }
        }
      } else {
        window.alert("Please enter both the name and type of the medicine.")
      }
      this.isSaving = false;
    },
    //delete drug from db
    deleteDrug: async function() {
      this.isDeleting = true
      const primaryKey = this.selectedMedicine.primaryKey
      const name = this.selectedMedicine.name
      const type = this.selected.title

      if (primaryKey !== "" && name !== "" && type !== "") {
        var data = { "primaryKey": primaryKey, "name": name, "type": type }
        await MedicineService.deleteMedicine(data);
        location.reload();
        return;
      }
      this.isDeleting = false;
    },
    //update drug
    updateDrug: async function() {
      this.isSaving = true
      const primaryKey = this.selectedMedicine.primaryKey
      const name = this.selectedMedicine.name
      const type = this.selected.title
      
      if (primaryKey !== "" && name !== "" && type !== "") {
        if (name.length > 40) {
          window.alert("Medicine name cannot be longer than 40 characters.")
        } else {
          if (this.regTest(name)) {
            window.alert("Medicine name contains invalid characters.")
          } else {
            var data = { "primaryKey": primaryKey, "name": name, "type": type }
            await MedicineService.updateMedicine(data);
            location.reload();
            return;
          }
        }
      } else{
        window.alert("Please enter both the name and type of the medicine.")
      }
      this.isSaving = false;
    },
    // This method will display the drug info
    // which user selected.
    changeInfo: function(medicine) {
      this.drugBox = true;
      this.changingDrug = true;
      this.addingDrug = false;
      if (medicine !== null) {
        this.showTable = true;
        this.showSaveButton = true;
        this.showDeleteButton = true;
        this.selectedMedicine = medicine;
        this.selected.title = medicine.type
      }
    },
    // This method will display the adding frame
    // which user can input info.
    addInfo: function() {
      this.drugBox = true;
      this.changingDrug = false;
      this.addingDrug = true;
      this.selected.title = "";
      this.showTable = true;
    }
  }
}
</script>

<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style lang="scss" scoped>
.table-layout{
  border-collapse: separate; 
  border-spacing: 0px 3px;
  
  .searchBox-layout{
    border-collapse: separate; 
    border-spacing: 5px;
  }
}

.search {
  border-bottom-left-radius: 0;
  border-bottom-right-radius: 0;
}

#all-drugs {
  width: 100%;
  height: 450px;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}

::v-deep .multiselect__content {
  .multiselect__option--highlight {
    background: #f3f3f3 !important;
    color: #35495e !important;
  }

  .option__image {
    max-width: 50px;
  }
}
</style>
