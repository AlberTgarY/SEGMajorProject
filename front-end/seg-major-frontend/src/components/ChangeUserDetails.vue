<template>
  <div id="change-user-details">
    <article class="message">
      <div class="message-header">
        <p>Change name</p>
      </div>
      <div class="message-body">
        <div class="field">
          <label class="label">Name</label>
          <div class="control">
            <input
              class="input"
              type="text"
              v-model="newName"
              :disabled="!user.name"
              @change="$v.newName.$touch()"
              @keyup.enter="changeUserName()"
              placeholder="Enter your name here..."
            />
          </div>
          <div v-if="$v.newName.$dirty">
            <p class="help is-danger" v-if="!$v.newName.required">This field is required</p>
          </div>
        </div>

        <div class="field is-grouped is-grouped-right">
          <p class="control">
            <button
              class="button is-success"
              @click="changeUserName()"
              :disabled="!user.name || newName == user.name"
            >Change name</button>
          </p>
        </div>
      </div>
    </article>

    <article id="change-email" class="message">
      <div class="message-header">
        <p>Change email</p>
      </div>
      <div class="message-body">
        <div class="field">
          <label class="label">Email</label>
          <div class="control">
            <input
              class="input"
              type="text"
              v-model="newEmail"
              :disabled="!user.email"
              @change="$v.newEmail.$touch()"
              @keyup.enter="changeEmail()"
              placeholder="Enter your email..."
            />
          </div>
          <div v-if="$v.newEmail.$dirty">
            <p class="help is-danger" v-if="!$v.newEmail.required">This field is required</p>
            <p class="help is-danger" v-if="!$v.newEmail.email">Please enter a valid email</p>
          </div>
        </div>

        <div class="field is-grouped is-grouped-right">
          <p class="control">
            <button
              class="button is-success"
              @click="changeEmail()"
              :disabled="$v.newEmail.$anyError || !newEmail || newEmail == user.email"
            >Change email</button>
          </p>
        </div>
      </div>
    </article>

    <article id="change-password" class="message">
      <div class="message-header">
        <p>Change password</p>
      </div>
      <div class="message-body">
        <div class="field">
          <label class="label">New password</label>
          <div class="control">
            <input
              class="input"
              type="password"
              v-model="newPassword"
              @change="$v.newPassword.$touch()"
              @keyup.enter="changePassword()"
              placeholder="Enter your new password..."
            />
          </div>
          <div v-if="$v.newPassword.$dirty">
            <p class="help is-danger" v-if="!$v.newPassword.required">This field is required</p>
            <p class="help is-danger" v-if="!$v.newPassword.minLength">Password too short</p>
          </div>
        </div>

        <div class="field is-grouped is-grouped-right">
          <p class="control">
            <button
              class="button is-success"
              @click="changePassword()"
              :disabled="$v.newPassword.$anyError || !newPassword"
            >Change password</button>
          </p>
        </div>
      </div>
    </article>
  </div>
</template>

<script>
import UserService from "@/services/user-service";

import { required, minLength, email } from "vuelidate/lib/validators";

export default {
  data() {
    return {
      user: {
        name: "",
        email: "",
        password: ""
      },
      newName: null,
      newPassword: null,
      newEmail: null
    };
  },
  validations: {
    newName: {
      required
    },
    newEmail: {
      required,
      email
    },
    newPassword: {
      required,
      minLength: minLength(5)
    }
  },
  methods: {
    async changeUserName() {
      this.$v.newName.$touch();
      if (this.$v.newName.$invalid) {
        return;
      }

      await UserService.changeName(this.newName);
      this.$emit("nameChanged", this.newName);
      this.user.name = this.newName;
    },
    async changeEmail() {
      this.$v.newEmail.$touch();
      if (this.$v.newEmail.$invalid) {
        return;
      }

      await UserService.changeEmail(this.newEmail);
      alert("Email changed successfully. Please re-login.");
      this.$router.push("/login?exp=true");
    },
    async changePassword() {
      this.$v.newPassword.$touch();
      if (this.$v.newPassword.$invalid) {
        return;
      }

      await UserService.changePassword(this.newPassword);
      this.newPassword = '';
      this.$v.newPassword.$reset();
    }
  },
  watch: {
    user() {
      this.newName = this.user.name;
      this.newEmail = this.user.email;
    }
  },
  async mounted() {
    this.user = await UserService.getUserDetails();
  }
};
</script>

<style lang="scss" scoped>
</style>