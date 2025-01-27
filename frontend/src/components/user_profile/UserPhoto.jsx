import React, { Component } from 'react';
import ContendCard from '../contents/ContentCard';
import { connect } from 'react-redux';

class UserPhoto extends Component {
    state = {
    }

    render() {
        return (
            <div className="container-fluid">
                <ContendCard>
                    <h4>User Avatar</h4>
                    <hr />
                </ContendCard>
            </div>
        );
    }
}

const mapStateToProps = (state, props) => {
    return {
        ...props,
        user_id: state.user_id,
        username: state.username,
        name: state.name,
        permission: state.permission,
        token: state.token,
        is_login: state.is_login,
    };
};

export default connect(mapStateToProps, null)((props) =>
    <UserPhoto
        {...props}
    />
);