<template>
  <div>
    <h2 id="page-heading" data-cy="ClipHeading">
      <span v-text="$t('clipsApp.clip.home.title')" id="clip-heading">Clips</span>
      <div class="d-flex justify-content-end">
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
    <div class="table-responsive" v-if="clips && clips.length > 0">
      <table class="table table-striped" aria-describedby="clips">
        <thead>
          <tr>
            <th scope="row"><span v-text="$t('global.field.id')">ID</span></th>
            <th scope="row"><span v-text="$t('clipsApp.clip.name')">Name</span></th>
            <th scope="row"><span v-text="$t('clipsApp.clip.content')">Content</span></th>
            <th scope="row"><span v-text="$t('clipsApp.clip.positiveCount')">Positive Count</span></th>
            <th scope="row"><span v-text="$t('clipsApp.clip.negativeCount')">Negative Count</span></th>
            <th scope="row"><span v-text="$t('clipsApp.clip.creator')">Creator</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="clip in clips" :key="clip.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ClipView', params: { clipId: clip.id } }">{{ clip.id }}</router-link>
            </td>
            <td>{{ clip.name }}</td>
            <td>
              <a v-if="clip.content" v-on:click="openFile(clip.contentContentType, clip.content)">
                <img v-bind:src="'data:' + clip.contentContentType + ';base64,' + clip.content" style="max-height: 30px" alt="clip image" />
              </a>
              <span v-if="clip.content">{{ clip.contentContentType }}, {{ byteSize(clip.content) }}</span>
            </td>
            <td>{{ clip.positiveCount }}</td>
            <td>{{ clip.negativeCount }}</td>
            <td>
              <div v-if="clip.creator">
                <router-link :to="{ name: 'ClipUserView', params: { clipUserId: clip.creator.id } }">{{ clip.creator.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ClipView', params: { clipId: clip.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ClipEdit', params: { clipId: clip.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(clip)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="$t('entity.action.delete')">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
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
