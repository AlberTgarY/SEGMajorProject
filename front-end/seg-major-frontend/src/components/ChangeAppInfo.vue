<template>
  <div id="change-app-info">
    <div class="field">
      <label class="label">Hospital Name</label>
      <div class="control">
        <input
          class="input"
          type="text"
          v-model="newAppInfo.hospitalName"
          v-bind:disabled="!appInfo.hospitalName"
          v-on:change="$v.newAppInfo.hospitalName.$touch()"
          v-on:keyup.enter="changeAppInfo()"
          placeholder="Enter hospital name..."
        />
      </div>
      <div v-if="$v.newAppInfo.hospitalName.$dirty">
        <p class="help is-danger" v-if="!$v.newAppInfo.hospitalName.required">This field is required</p>
      </div>
    </div>

    <div class="field">
      <label class="label">Department Name</label>
      <div class="control">
        <input
          class="input"
          type="text"
          v-model="newAppInfo.departmentName"
          v-bind:disabled="!appInfo.departmentName"
          v-on:change="$v.newAppInfo.departmentName.$touch()"
          v-on:keyup.enter="changeAppInfo()"
          placeholder="Enter department name..."
        />
      </div>
      <div v-if="$v.newAppInfo.departmentName.$dirty">
        <p
          class="help is-danger"
          v-if="!$v.newAppInfo.departmentName.required"
        >This field is required</p>
      </div>
    </div>

    <div class="field">
      <label class="label">Contact Details</label>
      <div class="control">
        <input
          class="input"
          type="text"
          v-model="newAppInfo.contactDetails"
          v-bind:disabled="!appInfo.contactDetails"
          v-on:change="$v.newAppInfo.contactDetails.$touch()"
          v-on:keyup.enter="changeAppInfo()"
          placeholder="Enter new contact details..."
        />
      </div>
      <div v-if="$v.newAppInfo.departmentName.$dirty">
        <p
          class="help is-danger"
          v-if="!$v.newAppInfo.contactDetails.required"
        >This field is required</p>
      </div>
    </div>

    <div class="field is-grouped is-grouped-right">
      <p class="control">
        <button
          class="button is-success"
          @click="changeAppInfo()"
          :disabled="$v.$anyError || !appInfo.departmentName || !appInfo.departmentName || !appInfo.contactDetails || (appInfo.hospitalName == newAppInfo.hospitalName && appInfo.departmentName == newAppInfo.departmentName && appInfo.contactDetails == newAppInfo.contactDetails)"
        >Save</button>
      </p>
    </div>
  </div>
</template>

<script>
import AppInfoService from "@/services/app-info-service";
import { required } from "vuelidate/lib/validators";

export default {
  data() {
    return {
      appInfo: {
        hospitalName: null,
        departmentName: null,
        contactDetails: null
      },
      newAppInfo: {
        hospitalName: null,
        departmentName: null,
        contactDetails: null
      }
    };
  },
  validations: {
    newAppInfo: {
      hospitalName: {
        required
      },
      departmentName: {
        required
      },
      contactDetails: {
        required
      }
    }
  },
  methods: {
    async changeAppInfo() {
      this.$v.$touch();
      if (this.$v.$invalid) return;

      await AppInfoService.updateAppInfo(this.newAppInfo);
      this.$emit("appInfoChanged", this.newAppInfo);
    }
  },
  watch: {
    appInfo() {
      this.newAppInfo = Object.assign({}, this.appInfo);
    }
  },
  async mounted() {
    this.appInfo = await AppInfoService.getAppInfo();
  }
};
</script>

<style lang="scss" scoped>
.field {
    height:100%;
    overflow:auto;
}
</style>