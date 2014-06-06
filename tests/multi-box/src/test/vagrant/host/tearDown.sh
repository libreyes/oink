#!/bin/bash
#
#
# Tears down three virtual machines needed for tests.
# 

set -e

pushd .
cd test-workspace/workspace
vagrant destroy --force

popd
pushd .
cd test-workspace/vagrant
vagrant destroy --force

popd

echo "COMPLETE"