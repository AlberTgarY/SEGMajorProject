<template>
  <div id="drug-chart-layout">
    <section class="section">
      <div class="container">
        <table class="table-layout">
          <tr>
            <td>
              <div class="field">
                <label class="label">Time</label>
                <div class="control">
                  <input class="input" type="time" id="time" name="appt" min="00:00" max="24:00" required>
                </div>
              </div>
              <drug-chart-search-box ref="drug" />
              <div class="field">
                <label class="label">Dose</label>
                <div class="control">
                  <div class="field has-addons">
                    <div class="control">
                      <input class="input" style="min-width:75px" type="number" id="dose" name="appt" min="0.0" step=".1" required>
                    </div>
                    <div class="control">
                      <div class="select">
                        <select id="unit">
                          <option value="mg">mg</option>
                          <option value="mls">mls</option>
                        </select>
                      </div>
                    </div>
                    <div class="control">
                      <div class="select">
                        <select id="freq">
                          <option value=""></option>
                          <option value="daily">daily</option>
                          <option value="daily for 1 week">daily for 1 week</option>
                          <option value="daily to continue">daily to continue</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="field">
                <div class="control">
                  <label class="checkbox" style="font-size:15px;">
                    <input type="checkbox" id="weaning" value="As per weaning regime">
                    As per weaning regime
                  </label>
                </div>
              </div>
              <div class="field is-grouped" style="margin-top:20px;">
                <div class="control is-expanded">
                  <button class="button is-success is-fullwidth" @click="addCard()">Add to chart</button>
                </div>
                <div class="control">
                  <button class="button is-info" v-print="printObj">
                    <span class="icon"><font-awesome-icon icon="print" /></span>
                    <span>Print</span>
                  </button>
                </div>
              </div>
            </td>
            <td class="vertical-separator"></td>
            <td>
              <!-- The printing unit -->
              <drug-chart-print-box ref="print" />
            </td>
          </tr>
        </table>
      </div>
    </section>
  </div>
</template>

<script>
import DrugChartSearchBox from "@/components/DrugChartSearchBox.vue"
import DrugChartPrintBox from "@/components/DrugChartPrintBox.vue"

export default{
  name: "DrugChartLayout",
  data() {
    return {
      // control unit for printing
      printObj: {
        id: "printableTable",
        popTitle: 'Drug Chart',
      }
    };
  },
  components: {
    DrugChartSearchBox,
    DrugChartPrintBox
  },
  methods: {
    /**
     * This method controls adding drug to printbox
     */ 
    addCard: function() {
      //get all the info it needs
      var time = document.getElementById("time").value;
      var dose = document.getElementById("dose").value.toString();
      var unit = document.getElementById("unit").value.toString();
      var freq = document.getElementById("freq").value.toString();
      var bold = document.getElementById("weaning").value.toString();
      var drug = this.$refs.drug.selectedDrugName;
      var items = this.$refs.print.getItems();

      if (time != null && drug != null && dose != null ) {
        var drugWithUnit = dose.concat(unit)
        //check if added drug is in the drug list
        if (drug !== "") {
            if (items.length < 100) {
              if (dose.length == 0 && !document.getElementById("weaning").checked) {
                window.alert("Please enter the dose");
              } else if (time.length != 0 && (drugWithUnit.length != 0 || document.getElementById("weaning").checked)) {
                if (document.getElementById("weaning").checked) {
                  dose = "";
                } else {
                  bold = "";
                }
                items.push({ Time: time, Drug: drug, Bold: bold, Unit: unit, Freq: freq, Dose: dose})
                items = items.sort(function (a, b) {
                  return a.Time > b.Time ? 1 : -1;
                });
              } else {
                window.alert("Please fill in all required fields");
              }
            } else { //can not print more than 100 medicines in one page.
              window.alert("You can only add up to 100 medicines")
            }
        } else {
          window.alert("Please select a medicine")
        }
      }
    },
  }
};
</script>
<style>
@media print {
  html, body {
    height: 100%;
    overflow: unset;
  }
}
</style>
<style lang="scss" scoped>

.table-layout{
  border-collapse: separate; 
  border-spacing: 15px 10px;
}

.vertical-separator {
  border-right: 1px #dbdbdb solid;
}
@media print {
  html, body {
    height: 100%;
    overflow: unset;
  }
}
</style>
