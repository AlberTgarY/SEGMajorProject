<template>
  <div id="admin-delete-account">
    <section class="hero is-danger">
      <div class="hero-body">
        <div class="custom-content-container">
          <h1 class="title is-4">Delete Your Account</h1>
        </div>
      </div>
    </section>

    <section class="section">
      <transition name="fade" mode="out-in">
        <loading-spinner v-if="!email" />
        <div class="custom-content-container" v-else>
          <div class="notification">
            <p class="has-text-weight-bold has-text-dark">To confirm, please enter your password.</p>
          </div>

          <br />

          <div class="field">
            <label class="label">Email</label>
            <div class="control">
              <input class="input" v-model="email" readonly type="text" />
            </div>
          </div>

          <div class="field">
            <label class="label">Password</label>
            <div class="control">
              <input
                class="input"
                type="password"
                v-model="password"
                placeholder="Enter password..."
                @change="$v.password.$touch()"
                @keyup.enter="deleteAccount()"
              />
            </div>
            <div v-if="$v.password.$dirty">
              <p class="help is-danger" v-if="!$v.password.required">This field is required</p>
              <p class="help is-danger" v-if="!$v.password.minLength">Password too short</p>
            </div>
          </div>

          <div class="level">
            <div class="level-left">
              <router-link to="/admin/settings" class="button is-light">Cancel</router-link>
            </div>
            <div class="level-right">
              <button
                class="button is-danger"
                :disabled="$v.$anyError || !password"
                @click="deleteAccount()"
              >Delete account</button>
            </div>
          </div>

          <div class="notification is-danger" v-if="error">
            <p>There was an error while trying to delete your account.</p>
            <p>Please re-enter your password and try again.</p>
          </div>
        </div>
      </transition>
    </section>
  </div>
</template>

<script>
import UserService from "@/services/user-service";
import LoadingSpinner from "@/components/LoadingSpinner";
import { required, minLength } from "vuelidate/lib/validators";

export default {
  components: {
    LoadingSpinner
  },
  data() {
    return {
      email: null,
      password: "",
      error: false
    };
  },
  metaInfo: {
    title: 'Delete Account'
  },
  validations: {
    password: {
      required,
      minLength: minLength(5)
    }
  },
  methods: {
    async deleteAccount() {
      this.$v.$touch();
      if (this.$v.$invalid) {
        return;
      }

      try {
        await UserService.deleteAccount({
          email: this.email,
          password: this.password
        });
        UserService.logout();
        this.$router.push("/");
      } catch (e) {
        this.error = true;
      }
    }
  },
  async mounted() {
    const user = await UserService.getUserDetails();
    this.email = user.email;
  }
};
</script>
<style lang="scss" scoped>
</style>