<template>
  <div>
    <h2 id="page-heading" data-cy="ClipHeading">
      <span v-text="$t('clipsApp.clip.home.title')" id="clip-heading">Clips</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="takeScreenshot" :disabled="isFetching">
          <span>TakeScreenshot</span>
        </button>
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('clipsApp.clip.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'ClipCreate' }" custom v-slot="{ navigate }">
          <button @click="navigate" id="jh-create-entity" data-cy="entityCreateButton" class="btn btn-primary jh-create-entity create-clip">
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('clipsApp.clip.home.createLabel')"> Create a new Clip </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && clips && clips.length === 0">
      <span v-text="$t('clipsApp.clip.home.notFound')">No clips found</span>
    </div>
    <div class="flex-container">
      <template v-for="(clips, index) in clips">
        <div class="card-container" :key="clips.id">
          <router-link :to="{ name: 'ClipView', params: { clipId: clips.id } }" custom v-slot="{ navigate }">
            <img
              @click="navigate"
              class="picture-item"
              v-bind:src="'data:' + clips.contentContentType + ';base64,' + clips.content"
              style="max-height: 210px"
            />
          </router-link>
          <div class="card-footer-flex">
            <div class="card-title-flex">{{ clips.name }}</div>
            <div class="rating">
              <div class="rating-up">
                <button class="rating-item" v-on:click="rateUp(index)">up</button>
                <div class="rate-up-number">{{ clips.positiveCount }}</div>
              </div>

              <div class="rating-down">
                <button class="rating-down-item" v-on:click="rateDown(index)">down</button>
                <div class="rate-down-number">{{ clips.negativeCount }}</div>
              </div>
            </div>
          </div>
          <button class="delete-button" v-on:click="prepareRemove(clips)" data-cy="entityDeleteButton" v-b-modal.removeEntity>X</button>
        </div>
      </template>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="clipsApp.clip.delete.question" data-cy="clipDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-clip-heading" v-text="$t('clipsApp.clip.delete.question', { id: removeId })">
          Are you sure you want to delete this Clip?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-clip"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeClip()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./clip.component.ts"></script>
