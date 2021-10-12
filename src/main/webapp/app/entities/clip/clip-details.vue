<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="clip">
        <h2 class="jh-entity-heading" data-cy="clipDetailsHeading">
          <span v-text="$t('clipsApp.clip.detail.title')">Clip</span> {{ clip.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('clipsApp.clip.name')">Name</span>
          </dt>
          <dd>
            <span>{{ clip.name }}</span>
          </dd>
          <dt>
            <span v-text="$t('clipsApp.clip.content')">Content</span>
          </dt>
          <dd>
            <div v-if="clip.content">
              <a v-on:click="openFile(clip.contentContentType, clip.content)">
                <img v-bind:src="'data:' + clip.contentContentType + ';base64,' + clip.content" style="max-width: 100%" alt="clip image" />
              </a>
              {{ clip.contentContentType }}, {{ byteSize(clip.content) }}
            </div>
          </dd>
          <dt>
            <span v-text="$t('clipsApp.clip.positiveCount')">Positive Count</span>
          </dt>
          <dd>
            <span>{{ clip.positiveCount }}</span>
          </dd>
          <dt>
            <span v-text="$t('clipsApp.clip.negativeCount')">Negative Count</span>
          </dt>
          <dd>
            <span>{{ clip.negativeCount }}</span>
          </dd>
          <dt>
            <span v-text="$t('clipsApp.clip.creator')">Creator</span>
          </dt>
          <dd>
            <div v-if="clip.creator">
              <router-link :to="{ name: 'ClipUserView', params: { clipUserId: clip.creator.id } }">{{ clip.creator.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="clip.id" :to="{ name: 'ClipEdit', params: { clipId: clip.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./clip-details.component.ts"></script>
