<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="clipsApp.clip.home.createOrEditLabel" data-cy="ClipCreateUpdateHeading" v-text="$t('clipsApp.clip.home.createOrEditLabel')">
          Create or edit a Clip
        </h2>
        <div>
          <div class="form-group" v-if="clip.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="clip.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('clipsApp.clip.name')" for="clip-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="clip-name"
              data-cy="name"
              :class="{ valid: !$v.clip.name.$invalid, invalid: $v.clip.name.$invalid }"
              v-model="$v.clip.name.$model"
              required
            />
            <div v-if="$v.clip.name.$anyDirty && $v.clip.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.clip.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('clipsApp.clip.content')" for="clip-content">Content</label>
            <div>
              <img
                v-bind:src="'data:' + clip.contentContentType + ';base64,' + clip.content"
                style="max-height: 100px"
                v-if="clip.content"
                alt="clip image"
              />
              <div v-if="clip.content" class="form-text text-danger clearfix">
                <span class="pull-left">{{ clip.contentContentType }}, {{ byteSize(clip.content) }}</span>
                <button
                  type="button"
                  v-on:click="clearInputImage('content', 'contentContentType', 'file_content')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <input
                type="file"
                ref="file_content"
                id="file_content"
                data-cy="content"
                v-on:change="setFileData($event, clip, 'content', true)"
                accept="image/*"
                v-text="$t('entity.action.addimage')"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="content"
              id="clip-content"
              data-cy="content"
              :class="{ valid: !$v.clip.content.$invalid, invalid: $v.clip.content.$invalid }"
              v-model="$v.clip.content.$model"
              required
            />
            <input
              type="hidden"
              class="form-control"
              name="contentContentType"
              id="clip-contentContentType"
              v-model="clip.contentContentType"
            />
            <div v-if="$v.clip.content.$anyDirty && $v.clip.content.$invalid">
              <small class="form-text text-danger" v-if="!$v.clip.content.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('clipsApp.clip.positiveCount')" for="clip-positiveCount">Positive Count</label>
            <input
              type="number"
              class="form-control"
              name="positiveCount"
              id="clip-positiveCount"
              data-cy="positiveCount"
              :class="{ valid: !$v.clip.positiveCount.$invalid, invalid: $v.clip.positiveCount.$invalid }"
              v-model.number="$v.clip.positiveCount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('clipsApp.clip.negativeCount')" for="clip-negativeCount">Negative Count</label>
            <input
              type="number"
              class="form-control"
              name="negativeCount"
              id="clip-negativeCount"
              data-cy="negativeCount"
              :class="{ valid: !$v.clip.negativeCount.$invalid, invalid: $v.clip.negativeCount.$invalid }"
              v-model.number="$v.clip.negativeCount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('clipsApp.clip.creator')" for="clip-creator">Creator</label>
            <select class="form-control" id="clip-creator" data-cy="creator" name="creator" v-model="clip.creator">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="clip.creator && clipUserOption.id === clip.creator.id ? clip.creator : clipUserOption"
                v-for="clipUserOption in clipUsers"
                :key="clipUserOption.id"
              >
                {{ clipUserOption.id }}
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
            :disabled="$v.clip.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./clip-update.component.ts"></script>
