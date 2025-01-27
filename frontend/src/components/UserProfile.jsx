import React, { Component } from 'react';
import { connect } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import ContendCard from './contents/ContentCard';
import ACTIONS from '../redux/actions';
import Brand from '../images/icon-with-name.png';

class UserProfile extends Component {
    state = {

    }


    render() {
        return (
            <React.Fragment>
                <div className="container">
                    <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                        <div className="col col-md-7">
                            <ContendCard>
                                <div className='d-flex justify-content-center mb-3'>
                                    <img src={Brand} className='img-fluid' alt="Brand" style={{ maxWidth: "50%" }}></img>
                                </div>
                                <hr />
                                <h4 className='text-center'>
                                    <span className="align-middle">
                                        User Management
                                    </span>
                                </h4>
                            </ContendCard>
                        </div>
                    </div>
                </div>
            </React.Fragment>
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

const mapDispatchToProps = {
    update_user: (data) => {
        return {
            type: ACTIONS.UPDATE_USER,
            user_id: data.user_id,
            username: data.username,
            name: data.name,
            permission: data.permission
        };
    },
};

export default connect(mapStateToProps, mapDispatchToProps)((props) =>
    <UserProfile
        {...props}
        navigate={useNavigate()}
    />
);