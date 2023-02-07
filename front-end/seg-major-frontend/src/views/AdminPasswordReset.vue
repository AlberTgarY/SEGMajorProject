<template>
  <section class="section">
    <div style="overflow: hidden" class="custom-content-container">
      <h1 class="title">Password Reset</h1>
      <div class="notification is-danger" v-if="resetFailed">
        <strong>Error:</strong> Reset password failed. Please check your token.
      </div>
      <div class="notification is-success" v-else-if="success">
        <strong>Success:</strong> Password has been reset.
      </div>
      <div>
        <transition name="slide" mode="out-in">
          <div class="field" id="step-one" key="step-one" v-if="!showStepTwo">
            <div class="field">
              <label class="label">Email</label>
              <div class="control">
                <input type="email" class="input" v-model="email">
              </div>
              <div v-if="$v.email.$dirty">
                <p class="help is-danger" v-if="!$v.email.required">This field is required</p>
                <p class="help is-danger" v-if="!$v.email.email">Please enter a valid email</p>
              </div>
            </div>
            <div class="field is-grouped">
              <div class="control">
                <button class="button is-primary" :class="{ 'is-loading': requestingToken }" @click="requestToken()">Request password reset</button>
              </div>
              <div class="control">
                <button class="button is-text no-underline" @click="alreadyHasToken()" :disabled="requestingToken">Already have a token?</button>
              </div>
            </div>
          </div>
          <div class="field" id="step-two" key="step-two" v-else>
            <div class="field" v-if="requestSent">
              <p>If the email address <b>{{ email }}</b> is associated with an account, you will receive an email with a reset token shortly. If the email does not arrive soon, please check your spam folder. 
                <transition name="fade"><a @click="doesNotHaveToken()" v-if="showRequestAgainPrompt">Didn't receive an email? Request it again.</a></transition>
              </p>
            </div>
            <div class="field">
              <label class="label">Reset token</label>
              <div class="control">
                <input type="text" class="input" v-model="token">
              </div>
              <p class="help">Find this in the email we sent you</p>
              <div v-if="$v.token.$dirty">
                <p class="help is-danger" v-if="!$v.token.required">This field is required</p>
              </div>
            </div>
            <div class="field">
              <label class="label">New password</label>
              <div class="control">
                <div class="field has-addons">
                  <div class="control is-expanded">
                    <input :type="showNewPassword ? 'text' : 'password'" class="input" v-model="newPassword">
                  </div>
                  <div class="control">
                    <button class="button" @click="showNewPassword = !showNewPassword">
                      <font-awesome-icon :icon="showPassword ? 'eye-slash' : 'eye'" />
                    </button>
                  </div>
                </div>
              </div>
              <div v-if="$v.newPassword.$dirty">
                <p class="help is-danger" v-if="!$v.newPassword.required">This field is required</p>
                <p class="help is-danger" v-if="!$v.newPassword.minLength">Password too short</p>
              </div>
            </div>
            <div class="field">
              <label class="label">New password again</label>
              <div class="control">
                <input type="password" class="input" v-model="newPasswordAgain">
              </div>
              <div v-if="$v.newPasswordAgain.$dirty">
                <p class="help is-danger" v-if="!$v.newPasswordAgain.required">This field is required</p>
                <p class="help is-danger" v-else-if="!$v.newPasswordAgain.sameAsNewPassword">Passwords do not match</p>
              </div>
            </div>
            <div class="field is-grouped">
              <div class="control">
                <button class="button is-primary" :class="{ 'is-loading': resettingPassword }" @click="resetPassword()">Reset password</button>
              </div>
              <div class="control">
                <button class="button is-text no-underline" @click="doesNotHaveToken()" v-if="!requestSent">Don't have a token?</button>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </section>
</template>

<script>
import UserService from '../services/user-service.js';
import AppInfoService from "@/services/app-info-service";
import { required, minLength, email, sameAs } from "vuelidate/lib/validators";

export default {
  data() {
    return {
      requestSent: false,
      showStepTwo: false,
      email: '',
      requestingToken: false,
      showRequestAgainPrompt: false,
      requestAgainPromptTimeout: -1,
      token: '',
      showNewPassword: false,
      newPassword: '',
      newPasswordAgain: '',
      resettingPassword: false,
      resetFailed: false,
      success: false,
      appInfo: {
        departmentName: ''
      },
    }
  },
  metaInfo() {
    return {
      title: 'Password Reset',
      titleTemplate: `%s - Admin | ${this.appInfo.departmentName}`,
      meta: [{ vmid: 'robots', name: 'robots', content: 'noindex' }]
    }
  },
  methods: {
    async requestToken() {
      this.$v.email.$touch();
      if (this.$v.email.$invalid) return;

      this.requestingToken = true;
      const email = this.email;

      await UserService.getResetRequest(email);

      this.requestSent = true;
      this.showStepTwo = true;
      this.requestingToken = false;
      this.requestAgainPromptTimeout = setTimeout(() => this.showRequestAgainPrompt = true, 15000);
    },
    alreadyHasToken() {
      this.requestingToken = false;
      this.requestSent = false;
      this.showStepTwo = true;
    },
    doesNotHaveToken() {
      if (this.requestAgainPromptTimeout !== -1) {
        clearTimeout(this.requestAgainPromptTimeout);
        this.requestAgainPromptTimeout = -1;
      }
      this.showStepTwo = false;
      this.requestSent = false;
    },
    async resetPassword() {
      this.$v.$touch();
      if (this.$v.token.$invalid || this.$v.newPassword.$invalid || this.$v.newPasswordAgain.$invalid) return;

      this.resettingPassword = true;
      const token = this.token;
      const newPassword = this.newPassword;

      try {
        await UserService.resetPassword(token, newPassword);
        this.resetFailed = false;
        this.success = true;
      } catch {
        this.resetFailed = true;
      }

      this.resettingPassword = false;
    },
  },
  validations: {
    email: {
      required,
      email
    },
    token: {
      required
    },
    newPassword: {
      required,
      minLength: minLength(5)
    },
    newPasswordAgain: {
      required,
      sameAsNewPassword: sameAs('newPassword')
    }
  },
  async created() {
    this.appInfo = await AppInfoService.getAppInfo();
  }
}
</script>

<style lang="scss" scoped>
.slide-enter-active, .slide-leave-active {
  transition: opacity 0.2s ease-out, transform 0.2s ease-out;
}

.slide-enter {
  opacity: 0;
  transform: translatex(12px);
}

.slide-leave-to {
  opacity: 0;
  transform: translatex(-12px);
}

.no-underline {
  text-decoration: none;
}

.custom-content-container {
  max-width: 550px;
}
</style>
