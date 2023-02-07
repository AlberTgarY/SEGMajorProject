<template>
  <div class="print" id="printableTable">
    <div class="header">
      <!-- Header of printbox -->
      <table class="table-layout striped">
        <tr>
          <th>
            <p>Patient Name</p>
            <input class="height input" size="45"/>
          </th>
          <th>
            <p>Hospital Number</p>
            <input class="height input" size="25"/>
          </th>
          <th>
            <p>DOB</p>
            <input class="height input" type="date"/>
          </th>
          <th>
            <p>Drug Allergies</p>
            <input class="height input" size="45"/>
          </th>
        </tr>
      </table>
    </div>
  
    <table class="print">
      <!-- The table displays all of the added medicines -->
      <tr>
        <table style="width: 960px;">
          <!-- table head -->
          <thead class="thead-light">
            <th valign="left">Time&nbsp;</th>
            <th valign="left">Medicine&nbsp;</th>
            <th style="width:90px;" valign="left">Dose&nbsp;<br></th>
          </thead>
            
          <tbody>
            <!-- drug names -->
            <tr class="print" valign="left" v-for="(item, index) in items" :index="index" :key="index">
              <td class="print" v-if="item === items[0]">{{ item.Time }}&nbsp;</td>
              <td class="print" v-else-if="items[index-1].Time === item.Time"></td>
              <td class="print" v-else>{{ item.Time }}&nbsp;</td>
              <td class="print">{{ item.Drug }}&nbsp;</td>
              <td class="print" v-if="item.Bold == 'As per weaning regime'"><b>{{ item.Bold }}&nbsp;</b></td>
              <td class="print" v-else><b>{{ item.Dose }}{{ item.Unit }}</b><br>{{ item.Freq }}&nbsp;</td>
              <!-- delete items -->
              <td class="print-hide" align="absmiddle"><button class="button" style="width: 10px;height: 30px;" @click="deleteItem(item)"><font-awesome-icon icon="times" /></button><br></td>
              <td class="print" style="width: 50px;" v-for="n in 20" :key="n"></td>
            </tr>
          </tbody>
        </table>
      </tr>
      <tbody style="display: none;">
        <tr>
          <!-- Footer for notification -->
          <div class="relative">
            <br><b>{{ appInfo.contactDetails }}</b>
          </div>
        </tr>
      </tbody>
    </table>
  </div>
</template>



<script>
import AppInfoService from "@/services/app-info-service";
export default {
  name: "DrugChartPrintBox",
  data() {
    return {
      appInfo: {
        hospitalName: '',
        departmentName: '',
        contactDetails: ''
      },
      //empty list for current added drugs
      items: [],
    }
  },
  async created() {
    this.appInfo = await AppInfoService.getAppInfo();
  },
  methods: {
    /**
     * @param {Object} item
     * Deleting items from print list
     */
    deleteItem: function(item) {
      var index = -1
      for (let i = 0; i < this.items.length; i++) {
        if (this.items[i] === item) {
          index = i
        }
      }
      if (index != -1) {
        this.items.splice(index, 1)
      }
    },
    getItems: function() {
      return this.items;
    }
  }
}
</script>


<style lang="scss" media="print" scoped>
/* Style the body */

.striped.table-layout {
  border-collapse: separate;
  border-spacing: 5px 25px;
}

.input.height {
  height: 25px;
}

/* Header/Logo Title */
.header {
  padding: 0px;
  text-align: center;
  color: white;
  font-size: 10px;
}

.content {
  padding: 20px;
}

.footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  color: black;
  text-align: center;
}

/* Style for the footer of print list */
div.relative {
  position: relative;
  width: 100%;
  bottom: 10px;
  color: black;
}

td.print {
  border: 1px solid #000000; 
}

tr.print {
  border: 1px solid #000000; 
}

@media print {
  .print-hide {
    visibility: hidden !important;
    display: none !important;
  }

  .page-break {
    page-break-after: always;
  }

  @page {
    size: landscape; 
    margin-top: 1mm;
  }
}
</style>
