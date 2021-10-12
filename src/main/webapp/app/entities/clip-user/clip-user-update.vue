<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="clipsApp.clipUser.home.createOrEditLabel"
          data-cy="ClipUserCreateUpdateHeading"
          v-text="$t('clipsApp.clipUser.home.createOrEditLabel')"
        >
          Create or edit a ClipUser
        </h2>
        <div>
          <div class="form-group" v-if="clipUser.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="clipUser.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('clipsApp.clipUser.internalUser')" for="clip-user-internalUser"
              >Internal User</label
            >
            <select
              class="form-control"
              id="clip-user-internalUser"
              data-cy="internalUser"
              name="internalUser"
              v-model="clipUser.internalUser"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="clipUser.internalUser && userOption.id === clipUser.internalUser.id ? clipUser.internalUser : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label v-text="$t('clipsApp.clipUser.ratedClips')" for="clip-user-ratedClips">Rated Clips</label>
            <select
              class="form-control"
              id="clip-user-ratedClips"
              data-cy="ratedClips"
              multiple
              name="ratedClips"
              v-if="clipUser.ratedClips !== undefined"
              v-model="clipUser.ratedClips"
            >
              <option v-bind:value="getSelected(clipUser.ratedClips, clipOption)" v-for="clipOption in clips" :key="clipOption.id">
                {{ clipOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.clipUser.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./clip-user-update.component.ts"></script>
